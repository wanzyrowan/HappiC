package heppic;

import java.util.List;
import java.io.FileInputStream; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;  
import java.util.ArrayList;
import opennlp.tools.postag.POSModel; 
import opennlp.tools.postag.POSSample; 
import opennlp.tools.postag.POSTaggerME; 
import opennlp.tools.tokenize.WhitespaceTokenizer; 

/**
 *
 * @author Evaldas Karpys
 */
public class ControlPoint {
    
        //init dictionary object - connect to sqllite in constructor
        //db contains key (sring/word) value (int/weight) pairs - stored in resources folder
        Dictionary dic;
    
    //constructor
    public ControlPoint () {
        this.dic = new Dictionary();
    }
    
    
    /**
 *
 * TagWords - tags words with their grammatical value using POS (Parts-of-speech) tagging
 * @param entered_words_org - list of original user input words
 * @return sample, a string with POS tagged entered_words (user input)
 */
    public String tagWords(List <String> entered_words_org) throws IOException, FileNotFoundException {
        
        //new list of entered words, contains entered_word_org-1 words; occasion e.g. Birthday is removed
        //we only use occasion to decide which code blocks will be executed
        ArrayList <String> entered_words = new ArrayList();
        
        //construct entered_words by removing occasion word from entered_words_org
        for(int i = entered_words_org.size()-1; i >= 0; i--){
            if (entered_words_org.get(i).equals("birthday") || entered_words_org.get(i).equals("christmas") || entered_words_org.get(i).equals("easter")){}
            else {entered_words.add(entered_words_org.get(i));}
        }

        //POS model
        InputStream inputStream = new FileInputStream("src\\resources\\tagger\\en-pos-maxent.bin");
        
        POSModel model = new POSModel(inputStream);
        
        POSTaggerME tagger = new POSTaggerME(model);
        
        //construct a string from entered_words list so it can be fed to POS
        String entered_words_split = constructString(entered_words);
        
        //Tokenizing the sentence using WhitespaceTokenizer class
        WhitespaceTokenizer whitespaceTokenizer= WhitespaceTokenizer.INSTANCE;
        String[] tokens = whitespaceTokenizer.tokenize(entered_words_split);
        
        //Generating tags
        String[] tags = tagger.tag(tokens);
        
        //Instantiating the POSSample class
        POSSample sample = new POSSample(tokens, tags);
        
        System.out.println("TAGGED " + sample.toString());
        
        return sample.toString();
        
    
    } //end of tagWords
    
    /**
 *
 * @param wordList - user input entered words without the occasion
 * method is called in tagWords method to construct a string of words from the list of user input words
 */
    private String constructString(List <String> wordList){
    
        String constructedString = "";
        
        for(int i = wordList.size()-1; i >= 0; i--){
            constructedString += wordList.get(i) + " ";
        }
        
    return constructedString;
    
    }
    
    
    /**
 *
 * @param rating - integer that user enters as a rating 1-5
 * @param entered_words - a list of user input words, used to determine ocassion
 * method runs through the list of entered words after user rating is submitted 
 * once one of the possible occasion words is found, the adjustWeights method in dictionary class is called with encountered occasion
 */
    public void adjustWeights(int rating, List <String> entered_words){
    
        for(int i = entered_words.size()-1; i >= 0; i--){
            if(entered_words.get(i).equals("birthday")){dic.adjustWeights(rating, "birthday");}
            if(entered_words.get(i).equals("christmas")){dic.adjustWeights(rating, "christmas");}
            if(entered_words.get(i).equals("easter")){dic.adjustWeights(rating, "easter");}
        }
    
    }
    
    
    /**
 * generateGreeting - for a given occasion produces a greeting following the POS tagged words
 * the greeting is generated given the cases of POS tags on the user input words and the number of input words
 * for 1 word input, it is expected to be an occasion, therefore generate a default greeting for each occasion
 * the creative part mostly comes from the variable selection of adjective to be placed in the greeting
 * there are 6 adjectives in each respectful occasion table, practically this number could be much larger thus providing a larger search space
 * @param entered_words_tagged - POS tagged user input words; does not contain the occasion
 * @param entered_words - list of user entered words; only used to determine the occasion
 * @return String = greeting string
 * 
 */
    public String generateGreeting(List <String> entered_words, String entered_words_tagged){
        
        String greeting = null;
    
        for(int i = entered_words.size()-1; i >= 0; i--){
            
            String current_word = entered_words.get(i).toLowerCase();
            
            //if occasion birthday
            if(current_word.equals("birthday")){
                
                switch (entered_words.size()){
                
                    //if only 1 word entered it must be the occassion name, therefore print default greeting
                    case 1:
                        greeting = "Have a " + dic.getHighestWeightWord("birthday") + " birthday!";
                        break;
                    case 2:
                        String [] word_and_tag_c2 = entered_words_tagged.split("_");
                        String word1_c2 = word_and_tag_c2[0];
                        String word1_tag_c2 = word_and_tag_c2[1];
                        //NN = noun; NNP = proper noun singular, expected in this case to be a name of some sort, person, place or country
                        if(word1_tag_c2.equals("NN") || word1_tag_c2.equals("NNP")){greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " birthday " + word1_c2 + "!";}
                        //CD = number
                        else if(word1_tag_c2.equals("CD")){greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " " + word1_c2 + " birthday!";}
                        else{greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " birthday " + word1_c2 + "!";}
                        break;
                    case 3:
                        String [] word_and_tag_full_string_c3 = entered_words_tagged.split(" ");
                        String [] word1_and_tag_c3 = word_and_tag_full_string_c3[0].split("_");
                        String word1_c3 = word1_and_tag_c3[0];
                        String word1_tag_c3 = word1_and_tag_c3[1];
                        String [] word2_and_tag_c3 = word_and_tag_full_string_c3[1].split("_");
                        String word2_c3 = word2_and_tag_c3[0];
                        String word2_tag_c3 = word2_and_tag_c3[1];
                        if(word1_tag_c3.equals("NN") || word1_tag_c3.equals("NNP")){
                            if(word2_tag_c3.equals("CD")){
                            greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " " + word2_c3 + " birthday " + word1_c3 + "!";
                            }
                            else if(word2_tag_c3.equals("NNS")){
                            greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " birthday " + word1_c3 + " with many " + word2_c3 +  "!";
                            }
                            else {greeting = "Have a " + dic.getHighestWeightWord("birthday") + " birthday!";}
                        }
                        else if(word1_tag_c3.equals("NNS")){
                            if(word2_tag_c3.equals("CD")){
                            greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " " + word2_c3 + " birthday " + " with many " + word1_c3 + "!";
                            }
                            else if(word2_tag_c3.equals("NN") || word2_tag_c3.equals("NNP")){
                            greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " birthday " + word2_c3 + " with many " + word1_c3 +  "!";
                            }
                            else {greeting = "Have a " + dic.getHighestWeightWord("birthday") + " birthday!";}
                        }
                        else if(word1_tag_c3.equals("CD")){
                            if(word2_tag_c3.equals("NN") || word2_tag_c3.equals("NNP")){
                            greeting = "Have a "+ dic.getHighestWeightWord("birthday") + " " + word1_c3 + " birthday " + word2_c3 + "!";
                            }
                        }
                        else {greeting = "Have a " + dic.getHighestWeightWord("birthday") + " birthday!";}
                        break;
                
                }
                
                
                return greeting;} //if occasion birthday END HERE
            
            //if occasion christmas
            if(current_word.equals("christmas")){
            switch (entered_words.size()){
                
                    //if only 1 word entered it must be the occassion name, therefore print default greeting
                    case 1:
                        greeting = "Have " + dic.getHighestWeightWord("christmas") + " Christmas!";
                        break;
                    case 2:
                        String [] word_and_tag_c2 = entered_words_tagged.split("_");
                        String word1_c2 = word_and_tag_c2[0];
                        String word1_tag_c2 = word_and_tag_c2[1];
                        //NN = noun; NNP = proper noun singular, expected in this case to be a name of some sort, person, place or country
                        if(word1_tag_c2.equals("NN") || word1_tag_c2.equals("NNP")){greeting = "Have "+ dic.getHighestWeightWord("christmas") + " christmas " + word1_c2 + "!";}
                        //CC = coordinating conjuction
                        else if(word1_tag_c2.equals("CC")){greeting = "Have "+ dic.getHighestWeightWord("christmas") + " christmas " + word1_c2 + " of you!";}
                        else{greeting = "Have "+ dic.getHighestWeightWord("christmas") + " christmas " + word1_c2 + "!";}
                        break;
                    case 3:
                        String [] word_and_tag_full_string_c3 = entered_words_tagged.split(" ");
                        String [] word1_and_tag_c3 = word_and_tag_full_string_c3[0].split("_");
                        String word1_c3 = word1_and_tag_c3[0];
                        String word1_tag_c3 = word1_and_tag_c3[1];
                        String [] word2_and_tag_c3 = word_and_tag_full_string_c3[1].split("_");
                        String word2_c3 = word2_and_tag_c3[0];
                        String word2_tag_c3 = word2_and_tag_c3[1];
                        if(word1_tag_c3.equals("NN") || word1_tag_c3.equals("NNP")){
                            if(word2_tag_c3.equals("NN") || word2_tag_c3.equals("NNP")){
                            greeting = "Have "+ dic.getHighestWeightWord("christmas") + " christmas " + word2_c3 + " and " + word1_c3 + "!";
                            }
                            else if(word2_tag_c3.equals("TO")){
                            greeting = "Have "+ dic.getHighestWeightWord("christmas") + " christmas " + word2_c3 + " " + word1_c3 + "!";
                            }
                            else {greeting = "Have " + dic.getHighestWeightWord("christmas") + " christmas!";}
                        }
                        
                        else if(word1_tag_c3.equals("CD")){
                            if(word2_tag_c3.equals("NN") || word2_tag_c3.equals("NNP")){
                            greeting = "Have "+ dic.getHighestWeightWord("christmas") + " " + word1_c3 + " christmas " + word2_c3 + "!";
                            }
                        }
                        else {greeting = "Have " + dic.getHighestWeightWord("christmas") + " christmas!";}
                        break;
                
                }
                
                
                return greeting;} //if occasion christmas END HERE
            
            //if occasion easter
            if(current_word.equals("easter")){
            switch (entered_words.size()){
                
                    //if only 1 word entered it must be the occassion name, therefore print default greeting
                    case 1:
                        greeting = "Have a " + dic.getHighestWeightWord("easter") + " Easter!";
                        break;
                    case 2:
                        String [] word_and_tag_c2 = entered_words_tagged.split("_");
                        String word1_c2 = word_and_tag_c2[0];
                        String word1_tag_c2 = word_and_tag_c2[1];
                        //NN = noun; NNP = proper noun singular, expected in this case to be a name of some sort, person, place or country
                        if(word1_tag_c2.equals("NN") || word1_tag_c2.equals("NNP")){greeting = "Have a "+ dic.getHighestWeightWord("easter") + " easter " + word1_c2 + "!";}
                        //CC = coordinating conjuction
                        else if(word1_tag_c2.equals("CC")){greeting = "Have "+ dic.getHighestWeightWord("easter") + " easter " + word1_c2 + " of you!";}
                        else{greeting = "Have a "+ dic.getHighestWeightWord("easter") + " easter " + word1_c2 + "!";}
                        break;
                    case 3:
                        String [] word_and_tag_full_string_c3 = entered_words_tagged.split(" ");
                        String [] word1_and_tag_c3 = word_and_tag_full_string_c3[0].split("_");
                        String word1_c3 = word1_and_tag_c3[0];
                        String word1_tag_c3 = word1_and_tag_c3[1];
                        String [] word2_and_tag_c3 = word_and_tag_full_string_c3[1].split("_");
                        String word2_c3 = word2_and_tag_c3[0];
                        String word2_tag_c3 = word2_and_tag_c3[1];
                        if(word1_tag_c3.equals("NN") || word1_tag_c3.equals("NNP")){
                            if(word2_tag_c3.equals("NN") || word2_tag_c3.equals("NNP")){
                            greeting = "Have "+ dic.getHighestWeightWord("easter") + " easter " + word2_c3 + " and " + word1_c3 + "!";
                            }
                            else if(word2_tag_c3.equals("TO")){
                            greeting = "Have "+ dic.getHighestWeightWord("easter") + " easter " + word2_c3 + " " + word1_c3 + "!";
                            }
                            else {greeting = "Have " + dic.getHighestWeightWord("easter") + " easter!";}
                        }
                        else if(word1_tag_c3.equals("CD")){
                            if(word2_tag_c3.equals("NN") || word2_tag_c3.equals("NNP")){
                            greeting = "Have a "+ dic.getHighestWeightWord("easter") + " " + word1_c3 + " easter " + word2_c3 + "!";
                            }
                        }
                        else {greeting = "Have " + dic.getHighestWeightWord("easter") + " easter!";}
                        break;
                
                }
                
                
                return greeting;} //if occasion easter END HERE

            
        }
        
        return "I'm unsure of the occassion. Check User Guide, in the top menu.";
    
    }
    
    
 /**
 * validate user input in the text field
 * @param: entered_words - the original list of user input words
 * note: don't need to check for empty list; with no inputs list yet contains [""]
 * @return boolean
 */
    public Boolean validateField(List <String> entered_words){
  
        //split entered_words string by comma and one or more whitespaces before/after comma
        //populate a list with the words split from the entered_words string
        //List<String> word_list = Arrays.asList(entered_words.split("\\s*,\\s*"));
        //check every element of the list if any of the elements is empty or a space
        //if true then returns false indicating that the input is not valid
        for(int i = entered_words.size()-1; i >= 0; i--){
            if(entered_words.get(i).equals("") || entered_words.get(i).equals(" ")) return false;
        }
        //allow 3 words at most
        if (entered_words.size() > 3){
            return false;
        }
        //if all good then return true indicating passed validation
        else {return true;}
    }
}
