package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 2;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    int wordLength = DEFAULT_WORD_LENGTH;
    private Random random = new Random();
    HashSet<String> wordSet;
    ArrayList<String> wordList;
    HashMap<String, ArrayList<String>> lettersToWord;

    public AnagramDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;
        wordSet = new HashSet<>();
        wordList = new ArrayList<>();
        lettersToWord = new HashMap<>();
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            wordSet.add(word);
            wordList.add(word);
            String wordnew = helper(word);

            ArrayList<String> values = new ArrayList<>();

            if (!lettersToWord.containsKey(wordnew)) {
                values.add(word);
                lettersToWord.put(wordnew, values);
            } else {
                values = lettersToWord.get(wordnew);
                values.add(word);
                lettersToWord.put(wordnew, values);
            }
        }
    }

    public String helper(String word) {
        char[] c = word.toCharArray();
        Arrays.sort(c);
        String s = new String(c);
        return s;
    }

    public boolean isGoodWord(String word, String base) {
        if (!word.contains(base) && wordSet.contains(word)) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        char[] alp = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : alp) {
            for (char d : alp) {
                String newword = c + word + d;
                String x = helper(newword);
                if (lettersToWord.containsKey(x)) {
                    result.addAll(lettersToWord.get(x));
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        Random r = new Random();
        while (true) {
            int x = r.nextInt(wordList.size());
            if (x <= wordList.size()) {
                String s = wordList.get(x);
                String k = helper(s);
                if (lettersToWord.get(k).size() >= 2) {
                    if (s.toCharArray().length == wordLength) {
                        wordLength++;
                        return s;
                    }
                }
            }
        }
    }

}
