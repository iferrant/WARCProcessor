package com.warcgenerator.core.task.generateCorpus.balancer;

import org.archive.io.ArchiveRecordHeader;

import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class CustomWarcWriter {
    private final String WARC_VERSION = "WARC/1.0";
    private final String WARC_CONTENT_TYPE_VALUE = "application/http;msgtype=response";
    private final String HEADER_WARCINFO_ALL_LANGUAGE = "WARC-All-Language";
    private final String HEADER_WARCINFO_ALL_CONTENT_TYPE = "WARC-All-Content_type";
    private final String WARCINFO_CONTENT = "WARCProcessor by SING Group www.sing-group.com";
    private final String HEADER_LANGUAGE = "WARC-Language";
    private final String HEADER_CONTENT_TYPE = "Content-Type";
    private final String[] warcInfoHeaderKeys = {"WARC-Type", "WARC-Date", "WARC-All-Language",
            "WARC-All-Content_type", "WARC-Record-ID", "Content-Type", "Content-Length"};
    private final String[] responseHeaderKeys = {"WARC-Type", "WARC-Target-URI", "WARC-Date",
            "WARC-Language", "WARC-Record-ID", "Content-Type", "Content-Length"};

    private Map<String, List<CustomHeader>> customHeaders = new HashMap<>();

    private int numPages;
    private String folderName;
    private File[] files;

    /**
     * Creates the {@link CustomWarcWriter} object with the folder that contains the warc files and the
     * number of pages that are going to be written
     * @param folder Folder of the warc files
     * @param numPages Number of pages to write of this <code>folder</code>
     */
    public CustomWarcWriter(File folder, int numPages) {
        this.folderName = folder.getName();
        this.files = getFilesOfFolder(folder);
        this.numPages = numPages;
    }

    /**
     * Writes the warc files
     * @throws IOException
     * @throws ParseException
     */
    public void writeWarcs() {
        try {
            List<WarcRecord> warcsList = buildListOfPages(files);
            List<WarcRecord> auxWarcsList = buildListOfPages(files);

            warcsList.forEach(this::setCustomHeaders);

            customHeaders.forEach((k, v) -> v.forEach(System.out::println));

            Iterator<WarcRecord> it = auxWarcsList.iterator();
            while (it.hasNext() && numPages != 0) {
                writeWarcFile(it.next());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an array of {@link File} with the files that contains the <code>folder</code>
     * @param folder Folder with files
     * @return Array of {@link File} objects
     */
    private File[] getFilesOfFolder(File folder) {
        File[] noFiles = {};
        return folder.isDirectory()? folder.listFiles(): noFiles;
    }

    /**
     * Creates a list of {@link WarcRecord} objects from the array of {@link File} <code>files</code>
     * @param files Array of {@link File}
     * @return List of {@link WarcRecord} objects
     * @throws IOException
     * @throws ParseException
     */
    private List<WarcRecord> buildListOfPages(File[] files) throws IOException, ParseException {
        List<WarcRecord> warcsList = new ArrayList<>();
        for (File item: files) {
            warcsList.add(getPagesOfWarc(item));
        }

        return warcsList;
    }

    /**
     * Builds a {@link WarcRecord} object from the <code>warc</code> received
     * @param warc File with the warc
     * @return {@link WarcRecord} object
     * @throws IOException
     * @throws ParseException
     */
    private WarcRecord getPagesOfWarc(File warc) throws IOException, ParseException {
        WARCReader warcReader = WARCReaderFactory.get(new File(warc.getAbsolutePath()));
        String warcFileName = warc.getName();
        ArchiveRecordHeader warcinfo = warcReader.get().getHeader();

        return new WarcRecord(warcFileName, warcinfo, warcReader.iterator());
    }

    /**
     * Writes the {@link WarcRecord} object into a .warc file, building the headers and creating the root folder
     * if its necessary
     * @param warcRecord {@link WarcRecord} instance
     */
    private void writeWarcFile(WarcRecord warcRecord) {
        // Create corpus directory if not exist
        createDirectory();
        File file = new File(folderName + "copy/" + warcRecord.getFileName());
        try (FileOutputStream os = new FileOutputStream(file)) {
            writeWarcInfoHeader(warcRecord.getFileName(), warcRecord.getWarcInfoHeader(), os);
            writeResponses(warcRecord.getPages(), os);
            lineBreak(os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes the warcinfo header into the <code>os</code>
     * @param fileName Name of the file
     * @param warcHeader {@link ArchiveRecordHeader} with the header to write
     * @param os {@link OutputStream} where write the header
     * @throws IOException
     */
    private void writeWarcInfoHeader(String fileName, ArchiveRecordHeader warcHeader, OutputStream os)
            throws IOException {
        os.write(WARC_VERSION.getBytes());
        lineBreak(os);
        for (String header: warcInfoHeaderKeys) {
            switch (header) {
                case HEADER_WARCINFO_ALL_LANGUAGE:
                    os.write(buildCustomHeader(fileName, header, customHeaders).getBytes());
                    break;
                case HEADER_WARCINFO_ALL_CONTENT_TYPE:
                    os.write(buildCustomHeader(fileName, header, customHeaders).getBytes());
                    break;
                default:
                    os.write(buildHeader(
                            header,
                            warcHeader.getHeaderValue(header))
                            .getBytes());
                    break;
            }
        }
        // Write warcinfo metadata
        lineBreak(os);
        os.write(WARCINFO_CONTENT.getBytes());
    }

    /**
     * Writes the warcresponse header into the <code>os</code>
     * @param warcHeader {@link ArchiveRecordHeader} with the header to write
     * @param os {@link OutputStream} where write the header
     * @throws IOException
     */
    private void writeResponseHeader(ArchiveRecordHeader warcHeader, OutputStream os) throws IOException {
        lineBreak(os);
        os.write(WARC_VERSION.getBytes());
        lineBreak(os);
        for (String header: responseHeaderKeys) {
            if (warcHeader.getHeaderValue(header) != null) {
                os.write(buildHeader(
                        header,
                        warcHeader.getHeaderValue(header))
                        .getBytes());
            }
        }
        lineBreak(os);
    }

    /**
     * Builds the warcinfo header with the <code>headerKey</code> key
     * @param fileName Name of the warc file
     * @param headerKey Key of the header to build
     * @param values Map the the file name as key and the list of {@link CustomHeader} as value
     * @return String with the warcinfo header built
     */
    private String buildCustomHeader(String fileName,
                                     String headerKey,
                                     Map<String, List<CustomHeader>> values) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<CustomHeader>> entry: values.entrySet()) {
            if (entry.getKey().equals(fileName)) {
                for (CustomHeader ch: entry.getValue()) {
                    if (ch.getKey().equals(headerKey)) {
                        for (String l : ch.getValues()) {
                            if (sb.length() != 0) {
                                sb.append(",");
                            }
                            sb.append(l);
                        }
                    }
                }
            }
        }
        return headerKey + ": " + sb.toString() + "\r\n";
    }

    /**
     * Builds a header with the <code>headerKey</code> as key and the <code>headerValue</code> as value
     * @param headerKey Key of the header
     * @param headerValue Value of the header
     * @return String object with the header built
     */
    private String buildHeader(String headerKey, Object headerValue) {
        String h = headerKey + ": ";
        if (headerKey.equals(HEADER_CONTENT_TYPE)) {
            h += WARC_CONTENT_TYPE_VALUE + "\r\n";
        } else {
            if (headerValue != null) {
                h += headerValue.toString() + "\r\n";
            } else {
                h += "\r\n";
            }
        }
        return h;
    }


    /**
     * Writes the {@link ArchiveRecord} content on the <code>os</code>
     * @param records Iterator of {@link ArchiveRecord}
     * @param os {@link OutputStream} where write the records
     * @throws IOException
     */
    private void writeResponses(Iterator<ArchiveRecord> records, OutputStream os) throws IOException {
        lineBreak(os);
        while (records.hasNext() && numPages != 0) {
            ArchiveRecord record = records.next();
            writeResponseHeader(record.getHeader(), os);
            record.dump(os);
            lineBreak(os);
            numPages--; // Just write the number of pages specified
        }
    }

    /**
     * Reads the warcresponse header of each record of the {@link WarcRecord} to save the custom headers values
     * @param warcRecord {@link WarcRecord} that needs to be edited
     */
    private void setCustomHeaders(WarcRecord warcRecord) {
        CustomHeader languages = new CustomHeader(HEADER_WARCINFO_ALL_LANGUAGE, new HashSet<>());
        CustomHeader contentTypes = new CustomHeader(HEADER_WARCINFO_ALL_CONTENT_TYPE, new HashSet<>());
        WarcRecord aux = new WarcRecord(warcRecord);
        while (aux.getPages().hasNext()) {
            ArchiveRecordHeader recordHeader = aux.getPages().next().getHeader();
            Object lang = recordHeader.getHeaderValue(HEADER_LANGUAGE);
            Object cont = recordHeader.getHeaderValue(HEADER_CONTENT_TYPE);
            if (lang != null) {
                languages.getValues().add(lang.toString());
            }
            if (cont != null) {
                contentTypes.getValues().add(cont.toString());
            }
        }
        List<CustomHeader> headers = new ArrayList<>();
        headers.add(languages);
        headers.add(contentTypes);
        customHeaders.put(warcRecord.getFileName(), headers);
    }

    /**
     * Writes a line break into the <code>os</code>
     * @param os {@link OutputStream} where write the line break
     * @throws IOException
     */
    private void lineBreak(OutputStream os) throws IOException {
        os.write("\r\n".getBytes());
    }

    /**
     * Creates the result folder if not exist
     */
    private void createDirectory() {
        String directoryName = folderName + "copy";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}

