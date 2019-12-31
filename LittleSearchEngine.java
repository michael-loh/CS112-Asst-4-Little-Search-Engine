package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		if(docFile == null){
			return null;
		}
		
		File file = new File(docFile);
		Scanner sc = new Scanner(file);
		
		HashMap<String, Occurrence> hMap = new HashMap<String, Occurrence>();
		
		//goes through the file, one line at a time
		while(sc.hasNext()){
			
			String word = sc.next();
			
			String keyWord = getKeyword(word);
			if(keyWord != null){
				
				if(!hMap.containsKey(keyWord)){
					Occurrence temp = new Occurrence(docFile, 1);
					hMap.put(keyWord, temp);
				}
				
				else{
					hMap.get(keyWord).frequency++;
				}
				
			}
			
		}
		
		
		return hMap;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		Set setOfKeys = kws.keySet();
		
		
		Iterator iterator = setOfKeys.iterator();
		
		
		while(iterator.hasNext()){
			
			String key = (String) iterator.next();
			
			if(keywordsIndex.containsKey(key)){
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			}
			else if(!keywordsIndex.containsKey(key)){
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(kws.get(key));
				keywordsIndex.put(key, occs);
				insertLastOccurrence(keywordsIndex.get(key));
			}
			
			
		}
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	private boolean isPunctuation(char a){
		if(a == '.' || a == ',' || a =='?' || a == ':' || a == ';' || a == '!'){
			return true;
		}
		return false;
	}
	
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		String editedWord = "";
		
		if(isPunctuation(word.charAt(word.length()-1))){
			editedWord = word.substring(0, word.length()-1);
		}
		else{
			editedWord = word;
		}
		
		editedWord = editedWord.toLowerCase();
		
		for(int i = 0; i < editedWord.length(); i++){
			if(!Character.isLetter(editedWord.charAt(i))){
				return null;
			}
		}
		
		if(noiseWords.contains(editedWord)){
			return null;
		}
		
		return editedWord;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		if(occs.size() == 0){
			return null;
		}
		
		if(occs.size() == 1){
			
			indexes.add(0);
			return indexes;
		}
		
		int left = 0;
		int right = occs.size()-2;
		int mid = 0;
		int occMid = occs.get(mid).frequency;
		String doc = occs.get(occs.size()-1).document;
		int frequency = occs.get(occs.size()-1).frequency;
		
		while(left <= right){
			
			
			mid = (right + left)/2;
			occMid = occs.get(mid).frequency;
			indexes.add(occMid);
			
			
			if(frequency == occMid)
				break;
			else if(frequency < occMid){
				left = mid + 1;
			}
			else if(frequency > occMid){
				right = mid - 1;
			}
			
		}
		
		int temp = 0;
		
		if(occs.size()==2){
			if(occs.get(0).frequency < occs.get(1).frequency){
				String tempOccString = occs.get(0).document;
				int tempOccFreq = occs.get(0).frequency;
				occs.get(0).frequency = occs.get(1).frequency;
				occs.get(0).document = occs.get(1).document;
				occs.get(1).document = tempOccString;
				occs.get(1).frequency = tempOccFreq;
			}
		}
		
		else{
			for(int i = occs.size()-1; i > mid; i--){
				Occurrence t2 = occs.get(i-1);
				occs.get(i).document = t2.document;
				occs.get(i).frequency = t2.frequency;
			}
		
			occs.get(mid).document = doc;
			occs.get(mid).frequency = frequency;
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return indexes;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw2 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		
		int kw1Size = 0;
		int kw2Size = 0;
		
		if(!keywordsIndex.containsKey(kw1)){
			kw1Size = keywordsIndex.get(kw1).size();
		}
		
		if(!keywordsIndex.containsKey(kw2)){
			kw2Size = keywordsIndex.get(kw2).size();
		}
		
		if(kw1Size >= 5){
			kw1Size = 5;
		}
		
		if(kw2Size >= 5){
			kw2Size = 5;
		}
		
		ArrayList<String>documents = null;
		int freq1 = 0;
		int freq2 = 0;
		if(keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)){
			
			int index1 = 0;
			int index2 = 0;
			
			while(index1 < kw1Size || index2 < kw2Size){
				//if we haven't traversed through both array lists yet
				if(index1 < kw1Size && index2 < kw2Size){
					if(keywordsIndex.get(kw1).get(index1).frequency >= keywordsIndex.get(kw2).get(index2).frequency){
						documents.add(keywordsIndex.get(index1).get(index1).document);
						index1++;
					}
					else{
						documents.add(keywordsIndex.get(index2).get(index2).document);
						index2++;
					}
				}
				else if(index1 < kw1Size){
					documents.add(keywordsIndex.get(kw1).get(index1).document);
					index1++;
				}
				else if(index1 > kw2Size){
					documents.add(keywordsIndex.get(kw2).get(index2).document);
					index2++;
				}
				
			}
			
			
		}
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return documents;
	
	}
}
