package com.digitalreasoning.token;

import com.digitalreasoning.entities.Sentence;
import com.digitalreasoning.serializer.BasicXMLSerializer;
import com.digitalreasoning.serializer.XmlSerializer;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lindsey on 9/20/16.
 */
public class BasicTokenizerBasicPhrases_UnitTest {
    BasicTokenizer tokenizer = new BasicTokenizer(false);

    XmlSerializer serializer = new BasicXMLSerializer();


    @Test
    public void tokenizeNumbers() throws IOException {
        reset();
        validate("340x180x90"           ,          "<sentence><token>340x180x90</token></sentence>"                                                                                  );
        validate("340 x 180 x 90"       ,          "<sentence><token>340</token><token>x</token><token>180</token><token>x</token><token>90</token></sentence>"                      );
        validate("2.12"                 ,          "<sentence><token>2.12</token></sentence>"                                                                                        );
        validate("$2.12"                ,          "<sentence><token>$</token><token>2.12</token></sentence>"                                                                         );
        validate("(21.5"                ,          "<sentence><token>(</token><token>21.5</token></sentence>"                                                                       );

        printResults();




    }

    @Test
    public void tokenizeWords() throws IOException {
        reset();
        validate("word ."               ,          "<sentence><token>word</token><token>.</token></sentence>"                                                                        );
        validate("word.cat"             ,          "<sentence><token>word.cat</token></sentence>"                                                                                      );
        validate("word ... "            ,          "<sentence><token>word</token><token>...</token></sentence>"                                                                      );
        validate("word."                ,          "<sentence><token>word</token><token>.</token></sentence>"                                                                       );
        validate("word..."              ,          "<sentence><token>word</token><token>...</token></sentence>"                                                                      );
        validate("word,."               ,          "<sentence><token>word</token><token>,</token><token>.</token></sentence>"                                                        );
        validate("word,..."             ,          "<sentence><token>word</token><token>,</token><token>...</token></sentence>"                                                      );
        validate("Euclid's"             ,          "<sentence><token>Euclid's</token></sentence>"                                                                                    );
        validate("(BFGS)"               ,          "<sentence><token>(</token><token>BFGS</token><token>)</token></sentence>"                                                        );
        validate("20th Century"         ,          "<sentence><token>20th</token><token>Century</token></sentence>"                                                                  );
        validate("Austria-Hungary"      ,          "<sentence><token>Austria-Hungary</token></sentence>"                                                                             );

    
        printResults();




    }
//    @Test
    public void notYetImplementedCases(){
        reset();
        validate("Austria-Hungary,"     ,          "<sentence><token>Austria-Hungary</token><token>,</token></sentence>"                                                                             );
        validate("Oracle USA, Inc."     ,          "<sentence><token>Oracle</token><token>USA</token><token>,</token><token>Inc.</token></sentence>"                                                                             );
        printResults();
    }
    @Test
    public void tokenizePunctuation() throws IOException {
        reset();
        validate("\"Word\""        ,   "<sentence><token>\"</token><token>Word</token><token>\"</token></sentence>"                              );
        validate("\"Word"          ,   "<sentence><token>\"</token><token>Word</token></sentence>"                                               );
        validate("Word\""          ,   "<sentence><token>Word</token><token>\"</token></sentence>"                                               );
        validate("Word,"           ,   "<sentence><token>Word</token><token>,</token></sentence>"                                                );
        validate(",Word"           ,   "<sentence><token>,</token><token>Word</token></sentence>"                                                );
        validate("Word:,"          ,   "<sentence><token>Word</token><token>:</token><token>,</token></sentence>"                                );
        validate("',,,'"           ,   "<sentence><token>'</token><token>,</token><token>,</token><token>,</token><token>'</token></sentence>"   );
        validate("..."             ,   "<sentence><token>...</token></sentence>"                                                                 );
        validate("word.\" "         ,   "<sentence><token>word</token><token>.</token><token>\"</token></sentence>"                                );

        printResults();

    }
    ////////////////--------------- helper methods
    private void reset() {
        results.clear();
    }

    private void validateSentenceParsing(String str, BasicTokenizer basicTokenizer, int expectedSentences, int expectedTokens) throws IOException {
        System.out.println(String.format ("Testing: '%s'",str));
        
        List<Sentence> sentences = basicTokenizer.tokenizeString(str);
        System.out.println( serializer.serializeSentences(sentences) );

        int totalTokens      = 0;
        for ( Sentence s: sentences){
            totalTokens += s.getTokens().size();
        }
        assertEquals( "Expected Sentences",expectedSentences ,sentences.size()  );
        assertEquals( "Expected Tokens"   ,expectedTokens   ,totalTokens       );
    }



    int maxPhraseLength,maxActualLength,maxExpectedLength;
    class Result{
        String phrase, expected, result;


    }
    List<Result> results = new ArrayList<Result>();
    private void validate(String phrase, String expected){
        Result r = new Result();
        r.phrase= phrase;
        r.expected = expected;

        try {
            r.result =  serializer.serializeSentences( tokenizer.tokenizeString(phrase) );
        } catch (IOException e) {
            r.result = "Exception";
            e.printStackTrace();
        }
        maxPhraseLength = Math.max(maxPhraseLength,r.phrase.length() + 2);
        maxActualLength = Math.max(maxActualLength,r.result.length() + 2);
        maxExpectedLength = Math.max(maxExpectedLength,r.expected.length() + 2);

        results.add(r);
    }
    private void printResults(){
        int failureCnt = 0;
        for (Result r :results){
            String formattedPhrase   = String.format("%-" + maxPhraseLength   + "s"  , "\""+r.phrase   + "\""  );
            String formattedExpected = String.format("%-" + maxExpectedLength + "s"  , "\""+r.expected + "\""  );
            String formattedActual   = String.format("%-" + maxActualLength   + "s"  , "\""+r.result   + "\""  );

            if ( !r.expected.equals( r.result) ){
                System.out.println(String.format("Failure %s:   Expected:%s  Actual:%s ", formattedPhrase, formattedExpected,formattedActual));
                failureCnt++;
            } else{
                System.out.println(String.format("Success %s:   Expected:%s ", formattedPhrase, formattedExpected,formattedActual) );
            }

        }

        if (failureCnt > 0){
            fail( String.format( "There were %s failures", failureCnt));
        }
    }
}