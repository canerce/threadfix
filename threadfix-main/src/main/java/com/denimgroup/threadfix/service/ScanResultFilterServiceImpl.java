package com.denimgroup.threadfix.service;

import com.denimgroup.threadfix.data.dao.ScanResultFilterDao;
import com.denimgroup.threadfix.data.entities.ChannelType;
import com.denimgroup.threadfix.data.entities.Finding;
import com.denimgroup.threadfix.data.entities.GenericSeverity;
import com.denimgroup.threadfix.data.entities.ScanResultFilter;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = false)
public class ScanResultFilterServiceImpl implements ScanResultFilterService{

    protected final SanitizedLogger log = new SanitizedLogger(ScanResultFilterServiceImpl.class);

    @Autowired
    private ScanResultFilterDao scanResultFilterDao;

    @Autowired
    private VulnerabilityService vulnerabilityService;

    @Autowired
    private FindingService findingService;

    @Autowired
    private VulnerabilityFilterService vulnerabilityFilterService;

    @Override
    public List<ScanResultFilter> loadAll() {
        return scanResultFilterDao.retrieveAll();
    }

    @Override
    public void storeAndApplyFilter(ScanResultFilter scanResultFilter) {
        storeAndApplyFilter(scanResultFilter, null, null);
    }

    @Override
    public void storeAndApplyFilter(ScanResultFilter scanResultFilter, GenericSeverity previousGenericSeverity, ChannelType previousChannelType) {

        if(previousGenericSeverity != null && previousChannelType != null
                && (!scanResultFilter.getGenericSeverity().equals(previousGenericSeverity) || !scanResultFilter.getChannelType().equals(previousChannelType))){

            unFilterFindings(previousGenericSeverity, previousChannelType);
        }

        scanResultFilterDao.saveOrUpdate(scanResultFilter);

        List<Finding> findingsToFilter = findingService.loadByGenericSeverityAndChannelType(scanResultFilter.getGenericSeverity(), scanResultFilter.getChannelType());

        if(findingsToFilter != null && !findingsToFilter.isEmpty()){
            for(Finding finding : findingsToFilter){
                finding.setHidden(true);
                findingService.storeFinding(finding);
            }
        }

        vulnerabilityFilterService.updateAllVulnerabilities();
    }

    private void unFilterFindings(GenericSeverity genericSeverity, ChannelType channelType){
        List<Finding> findingsToUnFilter = findingService.loadByGenericSeverityAndChannelType(genericSeverity, channelType);

        if(findingsToUnFilter != null && !findingsToUnFilter.isEmpty()){
            for(Finding finding : findingsToUnFilter){
                finding.setHidden(false);
                findingService.storeFinding(finding);
            }
        }
    }

    @Override
    public ScanResultFilter loadById(int scanResultFilterId) {
        return scanResultFilterDao.retrieveById(scanResultFilterId);
    }

    @Override
    public void delete(ScanResultFilter scanResultFilter) {

        unFilterFindings(scanResultFilter.getGenericSeverity(), scanResultFilter.getChannelType());
        vulnerabilityFilterService.updateAllVulnerabilities();

        scanResultFilterDao.delete(scanResultFilter);
    }

    @Override
    public List<GenericSeverity> loadFilteredSeveritiesForChannelType(ChannelType channelType) {
        return scanResultFilterDao.loadFilteredSeveritiesForChannelType(channelType);
    }

    @Override
    public ScanResultFilter loadByChannelTypeAndSeverity(ChannelType channelType, GenericSeverity genericSeverity) {
        return scanResultFilterDao.loadByChannelTypeAndSeverity(channelType, genericSeverity);
    }
}
