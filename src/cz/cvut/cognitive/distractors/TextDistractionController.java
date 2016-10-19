package cz.cvut.cognitive.distractors;

import cz.cvut.cognitive.load.CognitiveFunction;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



/**
 *
 * @author Johnny
 * 
 * Controller for TextDistraction
 * 
 */
public class TextDistractionController implements ScreenController  {
    
    private final int nOfLines = 4;

    private boolean messageBoxDialogHidden = true;
    private ListBox questionBox;
    private TextField answerField;
    private final Nifty nifty;
    private String [] setOfQuestions;
    private String [] setOfRightAnswers;
    private String [] userAnswers;
    private boolean isAnswer = false;
    private int lineCount;
    private int questionIndex;
    private int userAnswersArraySize = 50;
    private final String userAnswersFilePath = CognitiveFunction.saveHere + "/Answers.txt";
    private final String questionsFilePath = "assets/Interface/Text_questions/Questions.txt";
    private final SteeringCar car;
    private final Simulator sim;
    
    
        private TextDistraction textDistraction;
    
        //Constructor
    public TextDistractionController(TextDistraction textDistraction, Simulator sim) 
	{
		this.textDistraction = textDistraction;
                this.nifty = textDistraction.getNifty();
                this.sim = sim;
                this.car = sim.getCar();
	}

    @Override
    public void bind(Nifty nifty, Screen screen) {

    }

    /**
     * On startscreen initializes nifty-objects, presets answerbox
     */
    @Override
    public void onStartScreen() {
        Screen screen = nifty.getCurrentScreen();
        questionBox = (ListBox) screen.findNiftyControl("questionBox", ListBox.class);
        questionBox.changeSelectionMode(ListBox.SelectionMode.Disabled, false);	
        questionBox.setFocusable(false);
        
        answerField = screen.findNiftyControl("answerField", TextField.class);    
        //question = "So, let me get this straight, it is taking the combined might of all the heroes of Azeroth to defeat the Legion but it only took one Jesus?Talk about op. Nerf Jesus.";
        initQuestions(questionsFilePath);    
        sendToScreen();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userAnswersFilePath)))
        { 
         writer.write("Question:      Right Answer:     [Users Answer]:   ; Correctly answered?");
         writer.newLine();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
       
    }

    @Override
    public void onEndScreen() {
    }
    

    
    /**
     * Reads file with written questions and ads them to array for later call
     * @param filePath - path of file with questions
     */
    public void initQuestions(String filePath){
        lineCount = countLines(filePath);
        setOfQuestions = new String [lineCount];
        setOfRightAnswers = new String [lineCount];
        userAnswers = new String [userAnswersArraySize];
        
        try (Scanner scan = new Scanner(new File(filePath)))
        {
            for (int i = 0; i < lineCount; i++) {
            while (scan.hasNextLine()) {
                setOfRightAnswers[i] =" ";
                setOfQuestions[i] = "";
                    Scanner lineScan = new Scanner(scan.nextLine());          
                    String currentWord;
                    while (lineScan.hasNext()) {
                        
                        
                        
                        currentWord = lineScan.next();
                        if(currentWord.equals(";")){
                            isAnswer=true;
                            continue;
                        }
                        if(isAnswer){
                            setOfRightAnswers[i] = setOfRightAnswers[i].concat(currentWord);
                        } else{
                            setOfQuestions[i] =  setOfQuestions[i].concat(" "+currentWord);
                        }
                        
                         
                    }
                setOfRightAnswers[i] = setOfRightAnswers[i].substring(1);
                setOfQuestions[i] = setOfQuestions[i].substring(1);
                isAnswer = false;
                i++;
                
                }
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        } 
        
    }
    

    /**
     * Fills ListBox with current question. Method partially used from original
     * OpenDS team.
     * 
     */
    @SuppressWarnings("unchecked")
    public void sendToScreen(){
        //presets answerfield text
        answerField.setText("Replace this with your Answer and click Answer Button");
        answerField.setFocus();
        answerField.setCursorPosition(answerField.getRealText().length());
        
	// clear list box
	questionBox.clear();		
	int charactersPerLine = (int) (questionBox.getWidth()/7f);
			
	//selects random question to appear
        int n = (int)(Math.random() * lineCount);
        questionIndex = n;
        
        // split string word by word
	String[] words = setOfQuestions[n].trim().split(" ");
	int indexOfCurrentWord = 0;
			
	// fill line by line with words
	while(true) 
        {
            
            // initialize line
            String line = "";
			
            // try to get characters for one line
            try 
            {		

		// fill word by word into a line, until the maximum number of characters 
		// per line has been reached
					
		// length of first word in line
		int length = words[indexOfCurrentWord].length()+1;
		while(length <= charactersPerLine) 
                {
                    
                    // add current word
                    line += words[indexOfCurrentWord] + " ";
						
                    // go to next word
                    indexOfCurrentWord++;
						
                    // add length of next word for next loop
                    length += words[indexOfCurrentWord].length()+1;
                }
				
            } catch(Exception e){
		// ArrayIndexOutOfBoundsException will be caught, if not all lines filled
				}
				
				// add line to message box
            line = line.trim();
            if(!line.isEmpty())
            questionBox.addItem(line);
            
            else 
                break;
	}
            
        
        
    }
    
    
    /**
     * Counts lines of file with questions
     * @param file - file with questions
     * @return number of lines of question file
     */
    private static int countLines(String file){
        int lineCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            while (reader.readLine() != null) lineCount++;

        
        } catch (IOException e) {
			e.printStackTrace();
            } 
        return lineCount;
    }
    
    /**
     * After clicking answer button answer is stored to preset file
     */
    public void clickAnswerButton(){
        if(answerField.getRealText().equals("Replace this with your Answer") || answerField.getRealText().equals("")){
            System.out.println("Question wasn't answered");
        } else {
            userAnswers[questionIndex] = answerField.getRealText();
            answersToFile(answerField.getRealText(),userAnswersFilePath); 
            textDistraction.remove(); 
            
        }
    }
    
    /**
     * After clicking cancel button question is canceled and preset text is 
     * stored as answer (User clicked Cancel Button - no answer)
     */
    public void clickCancelButton()
    {
        answersToFile("User clicked Cancel Button - no answer",userAnswersFilePath);
	textDistraction.remove();   
    }
    
    /**
     * Stores answers of user from textfield
     * @param text - answer from field
     * @param fileName - path of answer file
     */
    private void answersToFile(String text, String fileName){

       try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true)))
       {
           int answeredRight;
           if (setOfRightAnswers[questionIndex].equalsIgnoreCase(text)){
               answeredRight = 1;
           } else answeredRight = 0;
           String currentAnswer = (setOfQuestions[questionIndex] 
                   + " " + setOfRightAnswers[questionIndex]
                   + " [" + text + "] ; " + answeredRight);
           writer.write(currentAnswer);
           writer.newLine();
           
       } catch (IOException e) {
			e.printStackTrace();
            } 
    }
    
}
