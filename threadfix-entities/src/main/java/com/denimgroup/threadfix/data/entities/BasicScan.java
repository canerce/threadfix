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
package com.denimgroup.threadfix.data.entities;

import com.denimgroup.threadfix.views.AllViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.map;

@Entity
@Table(name = "BasicScan")
public class BasicScan extends BaseEntity implements ScanLike{

	private static final long serialVersionUID = -8461350611851383656L;

	private ApplicationChannel applicationChannel;
	private Calendar importTime;
	private Application application;
	private Integer numberClosedVulnerabilities=0;
	private Integer numberNewVulnerabilities=0;
	private Integer numberOldVulnerabilities=0;
	private Integer numberResurfacedVulnerabilities=0;
	private Integer numberTotalVulnerabilities=0;
	private Integer numberHiddenVulnerabilities=0;

	private Long numberInfoVulnerabilities = 0L, numberLowVulnerabilities = 0L,
			numberMediumVulnerabilities = 0L, numberHighVulnerabilities = 0L,
            numberCriticalVulnerabilities = 0L;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "applicationChannelId")
    @JsonIgnore
    public ApplicationChannel getApplicationChannel() {
        return applicationChannel;
    }

    public void setApplicationChannel(ApplicationChannel applicationChannel) {
        this.applicationChannel = applicationChannel;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({AllViews.TableRow.class, AllViews.FormInfo.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class, AllViews.RestViewScanList.class})
	@Index(name="importTime")
    public Calendar getImportTime() {
        return importTime;
    }

    public void setImportTime(Calendar importTime) {
        this.importTime = importTime;
    }

    @ManyToOne
    @JoinColumn(name = "applicationId")
    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

	@Column
    @JsonView({AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class})
	public Integer getNumberClosedVulnerabilities() {
		return numberClosedVulnerabilities;
	}

	public void setNumberClosedVulnerabilities(Integer numberClosedVulnerabilities) {
		this.numberClosedVulnerabilities = numberClosedVulnerabilities;
	}

    @JsonView({AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class})
	@Column
	public Integer getNumberNewVulnerabilities() {
		return numberNewVulnerabilities;
	}

	public void setNumberNewVulnerabilities(Integer numberNewVulnerabilities) {
		this.numberNewVulnerabilities = numberNewVulnerabilities;
	}

    @JsonView({AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class})
	@Column
	public Integer getNumberOldVulnerabilities() {
		return numberOldVulnerabilities;
	}

	public void setNumberOldVulnerabilities(Integer numberOldVulnerabilities) {
		this.numberOldVulnerabilities = numberOldVulnerabilities;
	}

	@Column
    @JsonView({AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class})
    public Integer getNumberResurfacedVulnerabilities() {
		return numberResurfacedVulnerabilities;
	}

	public void setNumberResurfacedVulnerabilities(Integer numberResurfacedVulnerabilities) {
		this.numberResurfacedVulnerabilities = numberResurfacedVulnerabilities;
	}

	@Column
    @JsonView({ AllViews.TableRow.class, AllViews.FormInfo.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class })
    public Integer getNumberTotalVulnerabilities() {
		return numberTotalVulnerabilities;
	}

	public void setNumberTotalVulnerabilities(Integer numberTotalVulnerabilities) {
		this.numberTotalVulnerabilities = numberTotalVulnerabilities;
	}

	// These two functions establish the order the integers come in and this
	// order should not be changed.
	@Transient
	@JsonIgnore
	public List<Integer> getReportList() {
		List<Integer> integerList = new ArrayList<Integer>();
		integerList.add(getId());
		integerList.add(getNumberTotalVulnerabilities());
		integerList.add(getNumberNewVulnerabilities());
		integerList.add(getNumberOldVulnerabilities());
		integerList.add(getNumberResurfacedVulnerabilities());
		integerList.add(getNumberClosedVulnerabilities());
		return integerList;
	}

	@JsonIgnore
	public static ScanTimeComparator getTimeComparator() {
		return new ScanTimeComparator();
	}

    static public class ScanTimeComparator implements Comparator<ScanLike> {

		@Override
		public int compare(ScanLike scan1, ScanLike scan2){
			Calendar scan1Time = scan1.getImportTime();
			Calendar scan2Time = scan2.getImportTime();

			if (scan1Time == null || scan2Time == null) {
				return 0;
			}

			return scan1Time.compareTo(scan2Time);
		}
	}

	@Column
    @JsonView({ AllViews.TableRow.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class })
	public Long getNumberInfoVulnerabilities() {
		return numberInfoVulnerabilities;
	}

	public void setNumberInfoVulnerabilities(Long numberInfoVulnerabilities) {
		this.numberInfoVulnerabilities = numberInfoVulnerabilities;
	}

	@Column
    @JsonView({ AllViews.TableRow.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class })
	public Long getNumberLowVulnerabilities() {
		return numberLowVulnerabilities;
	}

	public void setNumberLowVulnerabilities(Long numberLowVulnerabilities) {
		this.numberLowVulnerabilities = numberLowVulnerabilities;
	}

	@Column
    @JsonView({ AllViews.TableRow.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class })
	public Long getNumberMediumVulnerabilities() {
		return numberMediumVulnerabilities;
	}

	public void setNumberMediumVulnerabilities(Long numberMediumVulnerabilities) {
		this.numberMediumVulnerabilities = numberMediumVulnerabilities;
	}

	@Column
    @JsonView({ AllViews.TableRow.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class })
	public Long getNumberHighVulnerabilities() {
		return numberHighVulnerabilities;
	}

	public void setNumberHighVulnerabilities(Long numberHighVulnerabilities) {
		this.numberHighVulnerabilities = numberHighVulnerabilities;
	}

	@Column
    @JsonView({ AllViews.TableRow.class, AllViews.RestView2_1.class, AllViews.RestViewScanStatistic.class })
    public Long getNumberCriticalVulnerabilities() {
		return numberCriticalVulnerabilities;
	}

	public void setNumberCriticalVulnerabilities(
			Long numberCriticalVulnerabilities) {
		this.numberCriticalVulnerabilities = numberCriticalVulnerabilities;
	}

	@Column
    @JsonView({AllViews.TableRow.class, AllViews.FormInfo.class, AllViews.RestViewScanStatistic.class})
    public Integer getNumberHiddenVulnerabilities() {
		if (numberHiddenVulnerabilities == null) {
			return 0;
		} else {
			return numberHiddenVulnerabilities;
		}
	}

	public void setNumberHiddenVulnerabilities(
			Integer numberHiddenVulnerabilities) {
		this.numberHiddenVulnerabilities = numberHiddenVulnerabilities;
	}

}
