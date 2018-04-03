package com.warcgenerator.core.helper;

import com.warcgenerator.core.config.CorpusSummary;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class CorpusValidatorHelper {

    /**
     * Check if the folder contains a Corpus based on the spam/ham folder names
     * @param corpusSummary {@link CorpusSummary} object with the corpus summary
     * @param corpusPath String with the path of the corpus
     * @return True if the spam and ham folder's name of the corpus
     *          summary matches the names of the real folders
     */
    public static boolean isCorpusFolder(CorpusSummary corpusSummary, String corpusPath) {
        String spamName = corpusPath + File.separator + corpusSummary.getSpamDir();
        String hamName = corpusPath + File.separator + corpusSummary.getHamDir();
        return (Files.exists(Paths.get(spamName), LinkOption.NOFOLLOW_LINKS)
                && Files.exists(Paths.get(hamName), LinkOption.NOFOLLOW_LINKS));
    }
}
