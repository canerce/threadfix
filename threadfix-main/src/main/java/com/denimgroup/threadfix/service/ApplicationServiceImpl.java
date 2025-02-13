////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.service;

import com.denimgroup.threadfix.data.dao.*;
import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.data.enums.EventAction;
import com.denimgroup.threadfix.data.enums.FrameworkType;
import com.denimgroup.threadfix.importer.util.IntegerUtils;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import com.denimgroup.threadfix.service.beans.TableSortBean;
import com.denimgroup.threadfix.service.defects.AbstractDefectTracker;
import com.denimgroup.threadfix.service.defects.DefectTrackerFactory;
import com.denimgroup.threadfix.service.repository.RepositoryServiceFactory;
import com.denimgroup.threadfix.service.util.PermissionUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.remote.response.RestResponse.success;
import static com.denimgroup.threadfix.util.ValidationUtils.HTML_ERROR;
import static com.denimgroup.threadfix.util.ValidationUtils.containsHTML;

@Service
@Transactional(readOnly = false)
public class ApplicationServiceImpl implements ApplicationService {
	
	private static final SanitizedLogger LOG = new SanitizedLogger(ApplicationServiceImpl.class);
	
	private String invalidProjectName = "The selected Project Name was invalid. " +
			"Either a non-existent project or a project with no components was selected.";
	
	private String invalidCredentials = "The User / password combination " +
			"(or possibly the Defect Tracker endpoint URL)";

	@Autowired private ApplicationDao applicationDao;
	@Autowired private DefectTrackerDao defectTrackerDao;
	@Autowired private RemoteProviderApplicationDao remoteProviderApplicationDao;
	@Autowired private WafRuleDao wafRuleDao;
	@Autowired private WafDao wafDao;
	@Autowired private VulnerabilityDao vulnerabilityDao;
	@Autowired private VulnerabilityService vulnerabilityService;
	@Autowired private AccessControlMapService accessControlMapService;
	@Autowired private ApplicationCriticalityDao applicationCriticalityDao;
	@Autowired private DefectDao defectDao;
	@Autowired private ScanMergeService scanMergeService;
	@Autowired private ScanDeleteService scanDeleteService;
    @Autowired private GenericVulnerabilityDao genericVulnerabilityDao;
    @Autowired private ScheduledScanDao scheduledScanDao;
    @Autowired private ApplicationCriticalityService applicationCriticalityService;
    @Autowired private DefaultDefectProfileServiceImpl defectProfileService;
	@Autowired private ApplicationVersionService applicationVersionService;

    @Nullable
    @Autowired(required = false)
    private ScanQueueTaskDao scanQueueTaskDao;

    @Nullable
    @Autowired(required = false)
    private PolicyService policyService;

    @Nullable
	@Autowired(required = false)
    private PermissionService permissionService;

    @Nullable
	@Autowired(required = false)
    private ScanQueueService scanQueueService;

    @Autowired
    private RepositoryServiceFactory repositoryServiceFactory;

    @Nullable
    @Autowired(required = false)
    private PolicyStatusService policyStatusService;

    @Override
	public List<Application> loadAllActive() {
		return applicationDao.retrieveAllActive();
	}
	
	@Override
	public List<Application> loadAllActiveFilter(Set<Integer> authenticatedTeamIds) {
		
		if (PermissionUtils.hasGlobalReadAccess()) {
			return loadAllActive();
		}
		
		if (authenticatedTeamIds == null || authenticatedTeamIds.size() == 0) {
			return list();
		}
		
		return applicationDao.retrieveAllActiveFilter(authenticatedTeamIds);
	}

	@Override
	public Application loadApplication(int applicationId) {
		return applicationDao.retrieveById(applicationId);
	}

	@Override
	public Application loadApplication(String applicationName, int teamId) {
		return applicationDao.retrieveByName(applicationName, teamId);
	}

	@Override
    public List<Application> loadApplicationByUniqueId(String uniqueId, int teamId) {
        return applicationDao.retrieveByUniqueId(uniqueId, teamId);
    }

    @Override
	@Transactional(readOnly = false)
	public void storeApplication(Application application, EventAction eventAction) {
		if (application != null) {
            // Set default for Application Type is Detect
            if (application.getFrameworkType().equals(FrameworkType.NONE.toString()))
                application.setFrameworkType(FrameworkType.DETECT.toString());
			applicationDao.saveOrUpdate(application);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deactivateApplication(Application application) {
        application.setActive(false);
        application.setModifiedDate(new Date());
        application.setTags(new ArrayList<Tag>());
        removeRemoteApplicationLinks(application);
        String possibleName = getNewName(application);

        if (application.getAccessControlApplicationMaps() != null) {
            for (AccessControlApplicationMap map : application.getAccessControlApplicationMaps()) {
                accessControlMapService.deactivate(map);
            }
        }

        if (scanQueueService != null && application.getScanQueueTasks() != null) {
            for (ScanQueueTask task : application.getScanQueueTasks()) {
                scanQueueService.deactivateTask(task);
            }
        }

        if (application.getGrcApplication() != null) {
            application.getGrcApplication().setApplication(null);
            application.setGrcApplication(null);
        }

        application.setWaf(null);

        // Delete WafRules attached with application
        deleteWafRules(application);

        // delete DefectTrackerProfiles attached with application
        deleteDefectTrackerProfilesByApplication(application);

        if (applicationDao.retrieveByName(possibleName, application.getOrganization().getId()) == null) {
            application.setName(possibleName);
        }

        if (scanQueueTaskDao != null) {
            for (ScanQueueTask scanQueueTask : application.getScanQueueTasks()) {
                scanQueueTaskDao.delete(scanQueueTask);
                scanQueueTask.setApplication(null);
            }
            application.setScanQueueTasks(null);
        }

		for (ScheduledScan scheduledScan : application.getScheduledScans()) {
			scheduledScanDao.delete(scheduledScan);
			scheduledScan.setApplication(null);
		}
		application.setScheduledScans(null);

		// Delete all versions
		for (ApplicationVersion version : application.getVersions()) {
			applicationVersionService.delete(version);
		}
		application.setVersions(null);


		application.getOrganization().updateVulnerabilityReport();

        if (policyStatusService != null && application.getPolicyStatuses() != null) {
            for (PolicyStatus policyStatus : application.getPolicyStatuses()) {
                policyStatusService.delete(policyStatus);
                policyStatus.setApplication(null);
            }
        }

		application.setPolicyStatuses(null);

		applicationDao.saveOrUpdate(application);

		// Delete Scans & Findings attached to application
		deleteScans(application);
	}

	private void deleteScans(Application application) {
		if (application.getScans() != null &&
                application.getScans().size() > 0) {
			scanDeleteService.deleteAllScanData(application);
		}
	}

    private void deleteWafRules(Application application) {
        if (application.getWaf() != null &&
                application.getVulnerabilities() != null) {
            for (Vulnerability vulnerability : application.getVulnerabilities()) {
                if (vulnerability != null && vulnerability.getWafRules() != null) {
                    // Since WAF Rules can only have one vulnerability, just delete them.
                    for (WafRule wafRule : vulnerability.getWafRules()) {
                        LOG.debug("Deleting WAF Rule with ID " + wafRule.getId()
								+ " because it was attached to the Vulnerability with ID " + vulnerability.getId() +
								" of application with ID " + application.getId());
                        wafRuleDao.delete(wafRule);
                    }
                }
                if (vulnerability != null) {
                    vulnerability.setWafRules(new ArrayList<WafRule>());
                }
            }
        }
    }

    private void deleteDefectTrackerProfilesByApplication(Application app) {
        DefectTracker defectTracker = app.getDefectTracker();
        if (defectTracker == null)
            return;

        List<Application> dtApps = defectTracker.getApplications();
        dtApps.remove(app);
        defectTracker.setApplications(dtApps);

        List<DefaultDefectProfile> deletedProfiles = list();
        List<DefaultDefectProfile> dtProfiles = defectTracker.getDefaultDefectProfiles();
        for (DefaultDefectProfile dtProfile : dtProfiles) {
            if (dtProfile.getReferenceApplication().getId().equals(app.getId())) {
                deletedProfiles.add(dtProfile);
            }
        }
        for (DefaultDefectProfile deletedProfile : deletedProfiles) {
            dtProfiles.remove(deletedProfile);
            defectProfileService.deleteProfileById(deletedProfile.getId());
        }
        defectTrackerDao.saveOrUpdate(defectTracker);
        app.setDefectTracker(null);
    }

	private String getNewName(Application application) {
		if (application != null) {
			
			String testString = "deleted-" + application.getId() + "-" + application.getName();
			if (testString.length() > Application.NAME_LENGTH) {
				testString = testString.substring(0, Application.NAME_LENGTH - 2);
			}
			return testString;
		}
		return null;
	}
	
	private void removeRemoteApplicationLinks(Application application) {
		if (application.getRemoteProviderApplications() != null &&
				application.getRemoteProviderApplications().size() > 0) {
			LOG.info("Removing remote applications from the application " + application.getName() +
					" (id=" + application.getId() + ")");
			for (RemoteProviderApplication app : application.getRemoteProviderApplications()) {
				LOG.info("Removing remote application " + app.getNativeName() +
						" from application " + app.getApplication().getName());
				app.setApplication(null);
				app.setLastImportTime(null);
				app.setApplicationChannel(null);
				remoteProviderApplicationDao.saveOrUpdate(app);
			}
		}
	}
	
	@Override
	public boolean validateApplicationDefectTracker(Application application,
			BindingResult result) {
		if (application == null || result == null) {
			return false;
		}
		
		if (application.getDefectTracker() != null &&
				(application.getDefectTracker().getId() == null
				|| application.getDefectTracker().getId() == 0)) {
			application.setDefectTracker(null);
			application.setUserName(null);
			application.setPassword(null);
		} else if (application.getDefectTracker() != null){
			DefectTracker defectTracker = defectTrackerDao.retrieveById(
					application.getDefectTracker().getId());
			if (defectTracker == null) {
				result.rejectValue("defectTracker.id", "errors.invalid",
						new String [] { "Defect Tracker choice" }, null);
				application.setUserName(null);
				application.setPassword(null);
				application.setProjectName(null);
			} else {
				
				application.setDefectTracker(defectTracker);
				AbstractDefectTracker dt = DefectTrackerFactory.getTracker(application);
				if (dt != null) {
					if (application.getUserName() == null
							|| application.getUserName().isEmpty()) {
						result.rejectValue("userName", "errors.required",
								new String [] { "User Name" }, null);
					}
					if (application.getPassword() == null
							|| application.getPassword().isEmpty()) {
						result.rejectValue("password", "errors.required",
								new String [] { "Password" }, null);
					}
					
					if (!result.hasErrors()) {
						if (!dt.hasValidCredentials()) {
							if (dt.getLastError() == null) {
								result.rejectValue("userName", "errors.invalid",
										new String [] { invalidCredentials }, null);
							} else {
								result.rejectValue("userName", "errors.detail",
										new String [] { dt.getLastError() }, null);
							}
							application.setUserName(null);
							application.setPassword(null);
							application.setProjectName(null);
						} else if (!dt.hasValidProjectName()) {
							result.rejectValue("projectName", "errors.detail",
									new String [] { invalidProjectName }, null);
							application.setProjectName(null);
						} else {
							encryptCredentials(application);
							application.setProjectId(dt.getProjectIdByName());
							return checkNewDefectTracker(application);
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param application
	 * @return true if the application has a different defect tracker
	 * 			than the database version, false otherwise
	 */
	private boolean checkNewDefectTracker(Application application) {
		if (application == null || application.getId() == null ||
				application.getDefectTracker() == null ||
				application.getDefectTracker().getId() == null) {
			return false;
		}
		
		Application databaseApplication = applicationDao.retrieveById(application.getId());
		
		if (databaseApplication == null || databaseApplication.getId() == null ||
				databaseApplication.getDefectTracker() == null ||
				databaseApplication.getDefectTracker().getId() == null) {
			return false;
		}
		
		return !application.getDefectTracker().getId().equals(
				databaseApplication.getDefectTracker().getId());
	}
	
	@Override
	public boolean checkApplication(Application application) {
		if (application == null || application.getName() == null
				|| application.getName().trim().isEmpty()
				|| application.getName().length() > Application.NAME_LENGTH) {
			LOG.warn("The application's name was invalid.");
			return false;
		}
		
		if (application.getUrl() != null &&
				application.getUrl().length() > Application.URL_LENGTH) {
			LOG.warn("The application's url was too long.");
			return false;
		}
		
		Application databaseApplication = loadApplication(application.getName().trim(), 
				application.getOrganization().getId());

		return databaseApplication == null;
	}
	
	@Override
	public void updateWafRules(Application application, Integer otherWafId) {
		if (application == null || application.getId() == null ||
				otherWafId == null) {
			return;
		}

		// if the new app doesn't have a WAF or the IDs don't match, need to remove the rules
		if (application.getWaf() == null ||
				application.getVulnerabilities() != null &&
				 application.getWaf().getId() != null &&
				 !otherWafId.equals(application.getWaf().getId())) {
			
			// Database vulns are still in session, also the vulns themselves
			// shouldn't have changed since we were only editing the information
			// about the Application object and not its vulnerabilities.
			for (Vulnerability vulnerability : application.getVulnerabilities()) {
				if (vulnerability != null && vulnerability.getWafRules() != null) {
					for (WafRule wafRule : vulnerability.getWafRules()) {
						wafRuleDao.delete(wafRule);
					}
					vulnerability.setWafRuleGeneratedTime(null);
					vulnerability.setWafRules(new ArrayList<WafRule>());
					vulnerabilityService.storeVulnerability(vulnerability);
				}
			}
		}
	}
	
	@Override
	public void validateAfterEdit(Application application, BindingResult result) {

		if (application.getName() == null || application.getName().trim().equals("")) {
			if (!result.hasFieldErrors("name")) {
				result.rejectValue("name", null, null, "This field cannot be blank");
			}
			return;
		}


		if (containsHTML(application.getName())) {
			LOG.error(HTML_ERROR);
			result.rejectValue("name", null, null, HTML_ERROR);
		}
		
		if (application.getRepositoryFolder() != null && !application.getRepositoryFolder().trim().equals("")) {
			File file = new File(application.getRepositoryFolder().trim());
			if (!file.exists() || !file.isDirectory()) {
				result.rejectValue("repositoryFolder", null, null, "Invalid directory");
				return;
			}
		}

        testRepositoryConnections(application, result);

		Application databaseApplication = decryptCredentials(loadApplication(application.getName().trim(),
                application.getOrganization().getId()));

		if (application.getApplicationCriticality() == null ||
				application.getApplicationCriticality().getId() == null ||
				applicationCriticalityDao.retrieveById(
						application.getApplicationCriticality().getId()) == null) {
			result.rejectValue("applicationCriticality.id", "errors.invalid",
					new String [] { "Criticality" }, null);
		}
		
		boolean canManageApplications = false;

        if (permissionService == null) { // not enterprise, which means everyone is admin
			canManageApplications = true;
        } else if (application.getOrganization() != null) {
			canManageApplications = permissionService.isAuthorized(Permission.CAN_MANAGE_APPLICATIONS,
					application.getOrganization().getId(), application.getId());
		}
		
		Application oldApp = loadApplication(application.getId());


        if (permissionService != null && application.getOrganization() != null && oldApp != null
                && application.getOrganization().getId() != oldApp.getOrganization().getId()) {
            if (!permissionService.isAuthorized(Permission.CAN_MANAGE_TEAMS, application.getOrganization().getId(), null)) {
                result.rejectValue("organization", null, null, "You don't have permission for this team.");
                return;
            }
        }

		if (oldApp != null && !canManageApplications) {
			application.setWaf(oldApp.getWaf());
			application.setDefectTracker(oldApp.getDefectTracker());
		}
		
		if (application.getWaf() != null && (application.getWaf().getId() == null ||
				application.getWaf().getId() == 0)) {
			application.setWaf(null);
		}
		
		if (application.getWaf() != null && application.getWaf().getId() != null) {
			Waf waf = wafDao.retrieveById(application.getWaf().getId());
			
			if (waf == null) {
				result.rejectValue("waf.id", "errors.invalid",
						new String [] { "WAF Choice" }, null);
			} else {
				application.setWaf(waf);
			}
		}
		
		// The password was set to the temp password so that it wouldn't show up on the page.
		// If the credentials haven't changed, re-decrypt the password to get the original.
		if (application.getPassword() != null && application.getPassword().equals(Application.TEMP_PASSWORD) &&
				databaseApplication != null && databaseApplication.getUserName() != null &&
				databaseApplication.getUserName().equals(application.getUserName()) &&
				databaseApplication.getDefectTracker() != null &&
				application.getDefectTracker() != null &&
				databaseApplication.getDefectTracker().getId().equals(
						application.getDefectTracker().getId())) {
			decryptCredentials(application);
		}
			
		if (databaseApplication != null && !databaseApplication.getId().equals(
				application.getId())) {
			result.rejectValue("name", "errors.nameTaken");
		}

		Integer databaseWafId = null;
		if (databaseApplication != null && databaseApplication.getWaf() != null) {
			databaseWafId = databaseApplication.getWaf().getId();
		}
		
		// remove any outdated vuln -> waf rule links
		updateWafRules(loadApplication(application.getId()), databaseWafId);

        encryptRepositoryCredentials(application);
	}

	@Override
	public void validateDefectTracker(Application application, BindingResult result) {
		boolean hasNewDefectTracker = validateApplicationDefectTracker(application, result);
		if (hasNewDefectTracker) application.setMainDefaultDefectProfile(null);
		
		if (hasNewDefectTracker || application.getDefectTracker() == null
				&& application.getDefectList() != null) {
			defectDao.deleteByApplicationId(application.getId());
		}
	}

    @Override
    public long getUnmappedFindingCount(Integer appId) {
        return applicationDao.getUnmappedFindingCount(appId);
    }

	@Override
	public long getApplicationCount() {
		return applicationDao.getApplicationCount();
	}

	@Override
	public void updateProjectRoot(Application application) {
		if (application != null && application.getProjectRoot() != null
				&& !application.getProjectRoot().trim().equals("")) {
			Application app = loadApplication(application.getId());
			
			scanMergeService.updateSurfaceLocation(app);
			scanMergeService.updateVulnerabilities(app, true);

			storeApplication(app, EventAction.APPLICATION_EDIT);
		}
	}
	
	@Override
	public void validateAfterCreate(Application application, BindingResult result) {
		
		if (application.getName() == null || application.getName().trim().equals("")) {
			
			if (!result.hasFieldErrors("name")) {
				result.rejectValue("name", null, null, "This field cannot be blank");
			}
			return;
		}

		if (containsHTML(application.getName())) {
			LOG.error(HTML_ERROR);
			result.rejectValue("name", null, null, HTML_ERROR);
		}
		
		if (application.getApplicationCriticality() == null ||
				application.getApplicationCriticality().getId() == null ||
				applicationCriticalityDao.retrieveById(
						application.getApplicationCriticality().getId()) == null) {
			result.rejectValue("applicationCriticality.id", "errors.invalid",
					new String [] { "Criticality" }, null);
		} else {
            application.getApplicationCriticality().setName(applicationCriticalityDao.retrieveById(
                    application.getApplicationCriticality().getId()).getName());
        }
		
		boolean canManageWafs = false, canManageDefectTrackers = false;

        if (permissionService == null) { // not enterprise, which means everyone is admin
            canManageDefectTrackers = true;
            canManageWafs = true;
        } else if (application.getOrganization() != null) {

			canManageWafs = permissionService.isAuthorized(Permission.CAN_MANAGE_WAFS,
					application.getOrganization().getId(), application.getId());
			
			canManageDefectTrackers = permissionService.isAuthorized(Permission.CAN_MANAGE_DEFECT_TRACKERS,
					application.getOrganization().getId(), application.getId());
		}

        testRepositoryConnections(application, result);

		Application databaseApplication = loadApplication(application.getName().trim(), application.getOrganization().getId());

        if (databaseApplication != null) {
			result.rejectValue("name", "errors.nameTaken");
			return;
		}

		if (application.getRepositoryFolder() != null && !application.getRepositoryFolder().trim().equals("")) {
			File file = new File(application.getRepositoryFolder().trim());
			if (!file.exists() || !file.isDirectory()) {
				result.rejectValue("repositoryFolder", null, null, "Invalid directory");
				return;
			}
		}
		
		if (!canManageWafs && application.getWaf() != null) {
			application.setWaf(null);
		}
		
		if (!canManageDefectTrackers && application.getDefectTracker() != null) {
			application.setDefectTracker(null);
		}

		if (application.getWaf() != null && application.getWaf().getId() == 0) {
			application.setWaf(null);
		}
		
		if (application.getWaf() != null && (application.getWaf().getId() == null
				|| wafDao.retrieveById(application.getWaf().getId()) == null)) {
			result.rejectValue("waf.id", "errors.invalid",
					new String [] { "WAF Choice" }, null);
		}

		validateApplicationDefectTracker(application, result);
        encryptRepositoryCredentials(application);
	}

    private void testRepositoryConnections(Application application, BindingResult result) {
        if (application.getRepositoryUrl() != null && !application.getRepositoryUrl().isEmpty()
                && application.getRepositoryType() != null) {

            RepositoryService repositoryService = repositoryServiceFactory.getRepositoryService(application);

            try {
                if (!repositoryService.testConfiguration(application)) {
                    result.rejectValue("repositoryUrl", null, null, "Unable to clone repository");
                }
            } catch (Exception e) {
                repositoryService.handleException(e, application, result);
            }
        }
    }

	@Override
	public List<Vulnerability> getVulnTable(int appId, TableSortBean bean) {
		if (bean != null) {
			int page = bean.getPage();
			int sort = bean.getSort();
			int field = bean.getField();
			
			String description = null, severity = null, path = null, param = null;
						
			if (bean.getDescriptionFilter() != null && !bean.getDescriptionFilter().trim().equals("")) {
				description = bean.getDescriptionFilter().trim();
			}
			
			if (bean.getSeverityFilter() != null && !bean.getSeverityFilter().trim().equals("")) {
				severity = bean.getSeverityFilter().trim();
			}
			
			if (bean.getLocationFilter() != null && !bean.getLocationFilter().trim().equals("")) {
				path = bean.getLocationFilter().trim();
			}
			
			if (bean.getParameterFilter() != null && !bean.getParameterFilter().trim().equals("")) {
				param = bean.getParameterFilter().trim();
			}
					
			Integer cweInteger = getCweId(bean);
			
			return vulnerabilityDao.retrieveActiveByAppIdAndPage(appId, page, sort, field, cweInteger,
										description, severity, path, param, bean.isOpen(),
										bean.isFalsePositive(), bean.isHidden());
		} else {
			return vulnerabilityDao.retrieveActiveByAppIdAndPage(appId, 1, 0, 0, null, null, null, null,
					null, true, false, false);
		}
	}
	
	private Integer getCweId(TableSortBean bean) {
		if (bean.getCweFilter() != null && !bean.getCweFilter().trim().equals("")) {
			String cwe = bean.getCweFilter().trim();

			Integer cweId = IntegerUtils.getIntegerOrNull(cwe);
            if (cweId != null) {
                GenericVulnerability genericVulnerability = genericVulnerabilityDao.retrieveByDisplayId(cweId);
                if (genericVulnerability != null)
                    return genericVulnerability.getId();
            }
		}
		
		return null;
	}
	
	@Override
	public long getCount(Integer appId, TableSortBean bean) {
		String description = null, severity = null, path = null, param = null;
		
		if (bean.getDescriptionFilter() != null && !bean.getDescriptionFilter().trim().equals("")) {
			description = bean.getDescriptionFilter().trim();
		}
		
		if (bean.getSeverityFilter() != null && !bean.getSeverityFilter().trim().equals("")) {
			severity = bean.getSeverityFilter().trim();
		}
		
		if (bean.getLocationFilter() != null && !bean.getLocationFilter().trim().equals("")) {
			path = bean.getLocationFilter().trim();
		}
		
		if (bean.getParameterFilter() != null && !bean.getParameterFilter().trim().equals("")) {
			param = bean.getParameterFilter().trim();
		}
		
		Integer cweInteger = getCweId(bean);
		
		return vulnerabilityDao.getVulnCountWithFilters(appId,description,severity,path,param, cweInteger,
														bean.isOpen(), bean.isFalsePositive(), bean.isHidden());
	}
	
	@Override
	public long getVulnCount(Integer appId, boolean open) {
		return vulnerabilityDao.getVulnCount(appId, open);
	}
	
	public Application encryptCredentials(Application application) {
		try {
			if (application != null && application.getPassword() != null && application.getUserName() != null) {
				application.setEncryptedPassword(ESAPI.encryptor().encrypt(application.getPassword()));
				application.setEncryptedUserName(ESAPI.encryptor().encrypt(application.getUserName()));
			}
		} catch (EncryptionException e) {
			LOG.warn("Encountered an ESAPI encryption exception. Check your ESAPI configuration.", e);
		}
		return application;
	}
	
	@Override
	public Application decryptCredentials(Application application) {
		if (application == null) {
			return null;
		}

		try {
			if (application.getEncryptedPassword() != null &&
					application.getEncryptedUserName() != null) {
				application.setPassword(ESAPI.encryptor().decrypt(application.getEncryptedPassword()));
				application.setUserName(ESAPI.encryptor().decrypt(application.getEncryptedUserName()));
			}

			DefectTracker defectTracker = application.getDefectTracker();
			if (defectTracker != null &&
					defectTracker.getEncryptedDefaultUsername() != null &&
					!defectTracker.getEncryptedDefaultUsername().isEmpty()
					&& defectTracker.getEncryptedDefaultPassword() != null &&
					!defectTracker.getEncryptedDefaultPassword().isEmpty()){

				defectTracker.setDefaultUsername(ESAPI.encryptor().decrypt(defectTracker.getEncryptedDefaultUsername()));
				defectTracker.setDefaultPassword(ESAPI.encryptor().decrypt(defectTracker.getEncryptedDefaultPassword()));
			}

		} catch (EncryptionException e) {
			LOG.warn("Encountered an ESAPI encryption exception. Check your ESAPI configuration.", e);
		}
		return application;
	}

    @Override
    public Application encryptRepositoryCredentials(Application application) {
        try {
            if (application != null && application.getRepositoryPassword() != null && !application.getRepositoryPassword().isEmpty()
                    && application.getRepositoryUserName() != null && !application.getRepositoryUserName().isEmpty()) {
                application.setRepositoryEncryptedPassword(ESAPI.encryptor().encrypt(application.getRepositoryPassword()));
                application.setRepositoryEncryptedUserName(ESAPI.encryptor().encrypt(application.getRepositoryUserName()));
            }

        } catch (EncryptionException e) {
            LOG.warn("Encountered an ESAPI encryption exception. Check your ESAPI configuration.", e);
        }
        return application;
    }

    @Override
    public Application decryptRepositoryCredentials(Application application) {
        try {
            if (application != null && application.getRepositoryEncryptedPassword() != null &&
                    application.getRepositoryEncryptedUserName() != null) {
                application.setRepositoryPassword(ESAPI.encryptor().decrypt(application.getRepositoryEncryptedPassword()));
                application.setRepositoryUserName(ESAPI.encryptor().decrypt(application.getRepositoryEncryptedUserName()));
            }
        } catch (EncryptionException e) {
            LOG.warn("Encountered an ESAPI encryption exception. Check your ESAPI configuration.", e);
        }
        return application;
    }

	@Override
	public void generateVulnerabilityReports(List<Organization> organizations) {
		if (organizations == null) {
			return;
		}
		for (Organization org : organizations) {
			generateVulnerabilityReports(org);
		}
	}
	
	@Override
	public void generateVulnerabilityReports(Organization organization) {
		if (organization == null || organization.getActiveApplications() == null) {
			return;
		}
		
		for (Application app : organization.getActiveApplications()) {
			generateVulnerabilityReport(app);
		}
	}
	
	public void generateVulnerabilityReport(Application application) {
		application.setVulnerabilityReport(applicationDao.loadVulnerabilityReport(application));
        application.getOrganization().updateVulnerabilityReport();

    }

    @Override
    public Object updateApplicationFromREST(Integer applicationId, MultiValueMap<String, String> params, BindingResult result) {
        Application dbApplication = loadApplication(applicationId);

        if(dbApplication == null){
            LOG.info("No application found for id: " + applicationId);
            throw new RuntimeException("No application found for id: " + applicationId);
        }

        if(params.containsKey("name")){
            dbApplication.setName((String) params.getFirst("name"));
        }
        if(params.containsKey("url")){
            dbApplication.setUrl((String) params.getFirst("url"));
        }
        if(params.containsKey("uniqueId")){
            dbApplication.setUniqueId((String) params.getFirst("uniqueId"));
        }
        if(params.containsKey("applicationCriticality")){
            dbApplication.setApplicationCriticality(applicationCriticalityService.loadApplicationCriticality(Integer.parseInt(params.getFirst("applicationCriticality"))));
        }
        if(params.containsKey("frameworkType")){
            dbApplication.setFrameworkType((String) params.getFirst("frameworkType"));
        }
        if(params.containsKey("repositoryUrl")){
            dbApplication.setRepositoryUrl((String) params.getFirst("repositoryUrl"));
        }
        if(params.containsKey("repositoryBranch")){
            dbApplication.setRepositoryBranch((String) params.getFirst("repositoryBranch"));
        }
        if(params.containsKey("repositoryUserName")){
            dbApplication.setRepositoryUserName((String) params.getFirst("repositoryUserName"));
        }
        if(params.containsKey("repositoryPassword")){
            dbApplication.setRepositoryPassword((String) params.getFirst("repositoryPassword"));
        }
        if(params.containsKey("repositoryFolder")){
            dbApplication.setRepositoryFolder((String) params.getFirst("repositoryFolder"));
        }

        validateAfterEdit(dbApplication, result);

        if(result.hasErrors()){
            throw new RuntimeException("Has validation errors");
        }

        storeApplication(dbApplication, EventAction.APPLICATION_EDIT);

        return success("Fields updated successfully.");
    }

    @Override
    public List<Policy> loadUnassociatedPolicies(Application application) {

        if (policyService == null)
            return list();

        List<Policy> policies = policyService.loadAll();

        if(policies != null && policies.size() > 0)
            policies.removeAll(application.getPolicies());

        return policies;
    }
}
