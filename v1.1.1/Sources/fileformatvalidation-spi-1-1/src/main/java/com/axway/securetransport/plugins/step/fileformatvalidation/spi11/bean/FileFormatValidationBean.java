/*
 * Copyright (c) Axway Software, 2022. All Rights Reserved.
 */
package com.axway.securetransport.plugins.step.fileformatvalidation.spi11.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@IsSeparatorValid()
public class FileFormatValidationBean {
    /** The type of the file e.g. (XML,CSV,DAT). */
    private String fileType;
    /** The desired quarantine folder for invalid files. */
    private String quarantineFolder;
    /** Separator for .csv file type. */
    private String separator;
    /** Number of expected elements in the .csv file. */
    private String elementNumber;
    /** Fixed length of the file record for .dat file. */
    private String fixedLength;
    /** Record header for the file for .dat file. */
    private String recordHeader;
    /** Forbidden characters in the .dat file. */
    private String forbiddenChars = ",";

  //Start of PSO engagement changes       
	private String skipFooter;
	private String skipHeader;
    private String headerCount;
    private String footerCountr;

    public String getHeaderCount() {
		return headerCount;
	}

	public void setHeaderCount(String headerCount) {
		this.headerCount = headerCount;
	}

	public String getFooterCountr() {
		return footerCountr;
	}

	public void setFooterCountr(String footerCountr) {
		this.footerCountr = footerCountr;
	}

	public String getSkipFooter() {
		return skipFooter;
	}

	public void setSkipFooter(String skipFooter) {
		this.skipFooter = skipFooter;
	}

	public String getSkipHeader() {
		return skipHeader;
	}

	public void setSkipHeader(String skipHeader) {
		this.skipHeader = skipHeader;
	}
	//End of PSO engagement changes       
    
    @Pattern(regexp = "^((xml)|(csv)|(dat))$", message = "Valid file type is xml, csv, dat.")
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @NotNull(message = "Quarantine Folder cannot be null.")
    @Size(min = 1, message = "Quarantine Folder cannot be empty.")
    public String getQuarantineFolder() {
        return quarantineFolder;
    }

    public void setQuarantineFolder(String quarantineFolder) {
        this.quarantineFolder = quarantineFolder;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getElementNumber() {
        return elementNumber;
    }

    public void setElementNumber(String elementNumber) {
        this.elementNumber = elementNumber;
    }

    public String getFixedLength() {
        return fixedLength;
    }

    public void setFixedLength(String fixedLength) {
        this.fixedLength = fixedLength;
    }

    public String getRecordHeader() {
        return recordHeader;
    }

    public void setRecordHeader(String recordHeader) {
        this.recordHeader = recordHeader;
    }

    public String getForbiddenChars() {
        return forbiddenChars;
    }

    public void setForbiddenChars(String forbiddenChars) {
        this.forbiddenChars = forbiddenChars;
    }

}
