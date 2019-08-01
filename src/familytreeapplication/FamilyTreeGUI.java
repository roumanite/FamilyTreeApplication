package familytreeapplication;
import person_data.Address;
import person_data.Person;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Comparator;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Modality;
import javafx.scene.control.Alert.AlertType; 
import javafx.scene.text.Text;

/**
 * This class stores the logic to process GUI. 
 * 
 * <p>
 * Title    : ICT373 Assignment 2 - Family Tree Application
 * Author   : Madyarini Grace Ariel
 * Date     : 3/7/2019
 * Filename : FamilyTreeGUI.java
 * Purpose  : Handles the GUI processing
 * 
 * @author madya
 */
public class FamilyTreeGUI extends Application
{
    // ID number
    private int counter = 0;
    
    // Layout Manager
    private ScrollPane treeView, infoView;
    private GridPane tree_layout = new GridPane();
    private VBox info_layout = new VBox();
    private HBox label_box = new HBox();
    
    // Button labels
    private String ADD_ROOT = "Add a Root Person";
    private String ADD_RELATIVE = "Add Immediate Relative"; 
    
    // Node labels
    private String LABEL_PARENTS = "Parents";
    private String LABEL_SPOUSE = "Spouse";
    private String LABEL_CHILDREN = "Children";
    private String LABEL_SIBLINGS = "Siblings";
    
    // Role text
    private String ROLE_FATHER = "Father";
    private String ROLE_MOTHER = "Mother";
    private String ROLE_SPOUSE = "Spouse";
    private String ROLE_CHILD = "Child";
    
    // Comparators
    private Comparator<TreeItem<Object>> compareNode;
    
    // Tracking unsaved changes
    private boolean saveNeeded = false;
    
    // Images
    private Image male;
    private Image female;
    private Image fileIcon;
    private Image treeIcon;
    
    // Tree
    private TreeView<Object> tree;
    
    private FamilyTreeLogic app = new FamilyTreeLogic();
    private Label filename_text; // filename
    
    /**
     * To be called by client application
     */
    public void run()
    {
        launch();
    }
    
    /**
     * Call createUI
     * @param topView - main stage
     */
    public void start(Stage topView)
    {
        createUI(topView);
    }
    
    /**
     * Display About message
     */
    private void displayAbout()
    {
        Stage aboutMsg = new Stage();
        aboutMsg.setTitle("Family Tree - About");
        VBox content = new VBox();
        content.setPadding(new Insets(10, 10, 10, 10));
        content.setAlignment(Pos.CENTER);
        Label textMsg = new Label("This application is created as a submission for ICT373 Assignment 2\nby Madyarini Grace Ariel from Murdoch University.");
        setCategoryStyle(textMsg);
        VBox.setMargin(textMsg, new Insets(10,10,10,10));
        Button ok = new Button("OK");
        VBox.setMargin(ok, new Insets(10,10,10,10));
        ok.setOnAction(e -> {aboutMsg.close();});
        content.getChildren().addAll(textMsg, ok);
        aboutMsg.setScene(new Scene(content));
        aboutMsg.initModality(Modality.APPLICATION_MODAL);
        aboutMsg.show();
    }
    
    /**
     * Turns text to image, is called if the image for the icon could not be loaded
     * @param text
     * @return WritableImage
     */
    private WritableImage textToImage(String text) 
    {
        Text t = new Text(text);
        Scene scene = new Scene(new StackPane(t));
        return t.snapshot(null, null);
    }
    
    /**
     * Establish main stage
     * @param topView - main stage
     */
    private void createUI(Stage topView)
    {
        topView.setTitle("Family Tree");
        VBox main_layout = new VBox();
        
        try{male = new Image(getClass().getResourceAsStream("img/male.png"), 16, 16, true, true);}
        catch(Exception e)
        {
            male = textToImage("(M)");
            System.out.println("Male icon failed to load : "+e);
        }
        
        try{female = new Image(getClass().getResourceAsStream("img/female.png"), 16, 16, true, true);}
        catch(Exception e)
        {
            female = textToImage("(F)");
            System.out.println("Female icon failed to load : "+e);
        }
        
        try{fileIcon = new Image(getClass().getResourceAsStream("img/file.png"), 16, 16, true, true);}
        catch(Exception e)
        {
            fileIcon = textToImage("#");
            System.out.println("File icon failed to load : "+e);
        }
        
        try
        {
            treeIcon = new Image(getClass().getResourceAsStream("img/tree.jpg"), 16, 16, true, true);
            topView.getIcons().add(treeIcon);
        }
        catch(Exception e)
        {
            treeIcon = textToImage("#");
            System.out.println("Tree icon failed to load : "+e);
        }
        
        // Create menu bar
        Menu file = new Menu("File");
        MenuItem newTree = new MenuItem("New Tree");
        newTree.setOnAction(e -> {
            if(!saveNeeded)
            {
                initFileName();
                initTreePane();
                initInfoPane();
            }
            else
            {
                ButtonType b1 = new ButtonType("Save");
                ButtonType[] arr = new ButtonType[3];
                arr[0] = b1;
                arr[1] = ButtonType.YES;
                arr[2] = ButtonType.CANCEL;
                Alert saveChanges = new Alert(AlertType.CONFIRMATION, "Are you sure you want to overwrite the current tree?", arr);
               
                saveChanges.showAndWait().ifPresent(response -> 
                {
                    if (response == ButtonType.YES) 
                        saveNeeded = false;
                    if (response == b1)
                    {
                        saveTreeFile(topView);
                        saveNeeded = false;
                    }
                    if(!saveNeeded)
                    {
                        initFileName();
                        initTreePane();
                        initInfoPane();
                    }
                });
            }
        });
        
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e ->{topView.close();});
        file.getItems().addAll(newTree, exit);
        
        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> displayAbout());
        help.getItems().add(about);
        
        MenuBar menubar = new MenuBar();
        
        menubar.getMenus().addAll(file, help);
        menubar.prefWidthProperty().bind(topView.widthProperty());
        
        // Create heading 
        VBox heading = new VBox(10);
        heading.getChildren().add(menubar);
        Label welcome_label = new Label("Welcome to Family Tree Application");
        welcome_label.setStyle("-fx-font-size:25");
        welcome_label.setTextFill(Color.web("#093B7A"));
        VBox.setMargin(welcome_label, new Insets(10,20,0,20));
        heading.setAlignment(Pos.CENTER);
        heading.getChildren().add(welcome_label);
        
        // Create main buttons
        HBox buttons = new HBox(30);
        Button loadTree = new Button("Load Tree");
        loadTree.setOnAction(e -> loadTreeFile(topView));
        Button saveTree = new Button("Save Tree");
        saveTree.setOnAction(e -> saveTreeFile(topView));
        Button addNewTree = new Button ("Create New Tree");
        addNewTree.setOnAction(e -> 
        {
            if(!saveNeeded)
            {
                initFileName();
                initTreePane();
                initInfoPane();
            }
            else
            {
                ButtonType b1 = new ButtonType("Save");
                ButtonType[] arr = new ButtonType[3];
                arr[0] = b1;
                arr[1] = ButtonType.YES;
                arr[2] = ButtonType.CANCEL;
                Alert saveChanges = new Alert(AlertType.CONFIRMATION, "Are you sure you want to overwrite the current tree?", arr);
               
                saveChanges.showAndWait().ifPresent(response -> 
                {
                    if (response == ButtonType.YES) 
                        saveNeeded = false;
                    if (response == b1)
                    {
                        saveTreeFile(topView);
                        saveNeeded = false;
                    }
                    if(!saveNeeded)
                    {
                        initFileName();
                        initTreePane();
                        initInfoPane();
                    }
                });
            }
        });
        
        buttons.getChildren().addAll(loadTree, saveTree, addNewTree);
        heading.getChildren().add(buttons);
        
        VBox.setMargin(buttons, new Insets(0,10,20,10));
        buttons.setAlignment(Pos.CENTER);
        
        main_layout.getChildren().add(heading);
        
        HBox center_box = new HBox();
        
        // Create left pane
        initFileName();
        initTreePane();
        VBox vb_tree = new VBox();
        treeView = new ScrollPane(tree_layout);
        
        treeView.setPrefViewportWidth(400);
        treeView.setPrefViewportHeight(450);
        
        main_layout.getChildren().add(center_box);
        vb_tree.getChildren().addAll(label_box, treeView);
        
        // Create right pane
        initInfoPane();
        label_box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-border-color: #093B7A;");
        label_box.setAlignment(Pos.CENTER);
        infoView = new ScrollPane(info_layout);
        infoView.setPadding(new Insets(20,20,20,20));
        infoView.prefWidthProperty().bind(topView.widthProperty());
        
        topView.setWidth(520);
        center_box.getChildren().addAll(vb_tree, infoView);
        
        Scene sc = new Scene(main_layout);
        
        topView.setScene(sc);
        
        topView.show();
        Comparator<String> compareLabels = new Comparator<String>() 
        {
            public int compare(String e1, String e2) 
            {
                int n1=0, n2=0;
                String[] arr = {LABEL_PARENTS, LABEL_SPOUSE, LABEL_CHILDREN, LABEL_SIBLINGS};
                for(int i = 0; i < arr.length; i++)
                {
                    if(arr[i].equals(e1))
                        n1 = i;
                    if(arr[i].equals(e2))
                        n2 = i;
                }
                return Integer.compare(n1,n2);
            }
        };
        compareNode = new Comparator<TreeItem<Object>>() 
        {
            public int compare(TreeItem<Object> e1, TreeItem<Object> e2) 
            {
                Object v1 = e1.getValue();
                Object v2 = e2.getValue();
                if(v1 instanceof String && v2 instanceof String)
                {
                    String s1 = (String)v1;
                    String s2 = (String)v2;
                    return compareLabels.compare(s1, s2);
                }
                else
                    return 0;
            }
        };
    }
    
    /**
     * When the tree is empty, the right pane shows "Load a tree or add a new root person"
     */
    private void initInfoPane()
    {
        clearInfoPane();
        Label info_label = new Label("Load a tree or add a new root person");
        info_label.setStyle("-fx-font-size:13");
        VBox.setMargin(info_label, new Insets(0,0,20,0));
        Button addRoot = new Button(ADD_ROOT);
        addRoot.setOnAction(e -> addMember(null));
        info_layout.getChildren().addAll(info_label, addRoot);
    }
    
    /**
     * When the tree is empty, the left pane shows an empty grid pane. 
     */
    private void initTreePane()
    {
        clearFileLabel();
        tree_layout.getChildren().clear();
        initTree();
        tree_layout.getChildren().add(tree);
        Node img = new ImageView(fileIcon);
        HBox.setMargin(filename_text, new Insets(5,0,5,10));
        HBox.setMargin(img, new Insets(5,0,5,0));
        filename_text.setStyle("-fx-font-size:13");
        label_box.getChildren().addAll(img, filename_text);
    }
    
    private void initFileName()
    {
        filename_text = new Label("Untitled");
    }
    
    /**
     * Clear file label
     */
    private void clearFileLabel()
    {
        if(label_box.getChildren() != null)
            label_box.getChildren().clear();
    }
    
    /**
     * Clear the right pane
     */
    private void clearInfoPane()
    {
        if(info_layout.getChildren() != null)
            info_layout.getChildren().clear();
    }
    
    /**
     * Set the label style for member information
     * @param nodes - Node which needs to be styled
     */
    private void setLabelStyle(Node... nodes)
    {
        for(Node node : nodes)
        {
            node.setStyle("-fx-font-size:12; -fx-font-weight:bold");
        }
    }
    
    /**
     * Add a new member related to subjectItem. If subjectItem is null, means the added person is a root
     * @param subjectItem - The old person that is related to the new person, null means new person is root
     */
    private void addMember(TreeItem<Object> subjectItem)
    {
        boolean addRoot = subjectItem == null;
        Label instruction;
        Stage addView = new Stage();
        
        VBox vb = new VBox();
        VBox err = new VBox();
        HBox err_box = new HBox();
        err_box.getChildren().add(err);
        err_box.setAlignment(Pos.CENTER);
        
        vb.setPadding(new Insets(20,20,20,20));
        
        if(addRoot) 
            instruction = new Label("Fill in the details for the root person below.");
        else
            instruction = new Label("Fill in the details for the relative below.");
        
        instruction.setStyle("-fx-font-size:20");
        
        Label info = new Label("(*) means mandatory field.");
        
        vb.getChildren().addAll(instruction, info, err_box);
        VBox.setMargin(instruction, new Insets(5,20,5,20));
        VBox.setMargin(info, new Insets(5,20,10,20));
        VBox.setMargin(err, new Insets(0,0,10,0));
        
        Label fName_label = new Label("First Name* : ");
        TextField fName = new TextField();
        
        Label surname_label = new Label("Surname : ");
        TextField surname = new TextField();
        
        Label maiden_label = new Label("Maiden name : ");
        TextField maidenName = new TextField();
        
        Label gender_label = new Label("Gender* : ");
        ChoiceBox<String> gender = new ChoiceBox<>();
        gender.getItems().addAll("Male", "Female");
  
        Label desc_label = new Label("Life description : ");
        TextArea desc = new TextArea();
        desc.setPrefRowCount(3);
        desc.setPrefColumnCount(3);
        
        Label no_label = new Label("Street no.* : ");
        TextField streetNo = new TextField();
        
        Label name_label = new Label("Street name* : ");
        TextField streetName = new TextField();
        
        Label suburb_label = new Label("Suburb name* : ");
        TextField suburbName = new TextField();
        
        Label postal_label = new Label("Postal code* : ");
        TextField postalCode = new TextField();
        
        Button submit = new Button("Submit");
        
        if(!addRoot)
        {
            Person subjectPerson = (Person)subjectItem.getValue();
            
            Label relation = new Label("How is the new family member related to "+subjectPerson.getFName()+" "+subjectPerson.getSurname()+"?");
            ChoiceBox relation_type = new ChoiceBox();
            
            VBox.setMargin(relation, new Insets(0,0,0,10));
            VBox.setMargin(relation_type, new Insets(0,10,10,10));
            
            if(subjectItem.getParent() != null)
            {
                String category = (String)subjectItem.getParent().getValue();
                if(category.equals(LABEL_PARENTS) || category.equals(LABEL_SPOUSE))
                    relation_type.getItems().addAll(ROLE_CHILD); // We can only add child for root's parents which will be labeled as sibling
                else
                    relation_type.getItems().addAll(ROLE_SPOUSE, ROLE_CHILD);
            }
            else
                relation_type.getItems().addAll(ROLE_FATHER, ROLE_MOTHER, ROLE_SPOUSE, ROLE_CHILD); // We can add Father, Mother, Spouse, and Child related to the root
            
            // add a listener 
            relation_type.getSelectionModel().selectedItemProperty().addListener(e -> 
            { 
                String choice = relation_type.getSelectionModel().getSelectedItem().toString();
                gender.setDisable(true);
                if(choice.equals(ROLE_FATHER))
                    gender.setValue("Male");
                else if(choice.equals(ROLE_MOTHER))
                    gender.setValue("Female");
                else if(choice.equals(ROLE_SPOUSE))
                {
                    if(subjectPerson.getGender() == 'M') // opposite gender
                        gender.setValue("Female");
                    else
                        gender.setValue("Male");
                }
                else
                    gender.setDisable(false);
            }); 
            vb.getChildren().addAll(relation, relation_type);
            
            submit.setOnAction(e -> 
            {
                err.getChildren().clear();
                LinkedHashMap<String, String> input = new LinkedHashMap<>();
                
                String type = "";
                
                if(relation_type.getValue() != null)
                    type = relation_type.getValue().toString();
                
                input.put("relation type", type);
                input.put("first name", fName.getText());
                input.put("surname", surname.getText());

                if(gender.getValue() == null)
                    input.put("gender", "");
                else
                    input.put("gender", gender.getValue());

                input.put("maiden name", maidenName.getText());
                input.put("life description", desc.getText());
                input.put("street number", streetNo.getText());
                input.put("street name", streetName.getText());
                input.put("suburb name", suburbName.getText());
                input.put("postal code", postalCode.getText());

                String msg = validate(input);
                if(msg.isEmpty())
                {
                    int stNo = Integer.parseInt(streetNo.getText());
                    char g = gender.getValue().equals("Male") ? 'M' : 'F';
                    Node image = g == 'M' ? new ImageView(male) : new ImageView(female);
                    counter++;
                    String id = Integer.toString(counter);
                    Address addr = new Address(stNo, streetName.getText(), suburbName.getText(), postalCode.getText());
                    Person newPerson = new Person(id, fName.getText(), surname.getText(), maidenName.getText(), g, addr, desc.getText());
                  
                    TreeItem<Object> newNode = new TreeItem<>(newPerson, image);
                    String confirm = "Are you sure you want to replace ";
                    ButtonType b1 = new ButtonType("Save");
                    ButtonType[] arr = new ButtonType[2];
                    arr[0] = ButtonType.YES;
                    arr[1] = ButtonType.CANCEL;
                    Alert replace;
                    if(type.equals(ROLE_FATHER))
                    {
                        Person father = subjectPerson.getFather();
                        if(father != null)
                        {
                            replace = new Alert(AlertType.CONFIRMATION, confirm+father.getFName()+" "+father.getSurname()+" as father?", arr);

                            replace.showAndWait().ifPresent(response -> 
                            {
                                if (response == ButtonType.YES)
                                {
                                    removeNode(subjectItem, ROLE_FATHER);
                                    subjectPerson.setFather(newPerson);
                                    addUnderLabel(LABEL_PARENTS, subjectItem, newNode);
                                } 
                            });
                        }
                        else
                        {
                            subjectPerson.setFather(newPerson);
                            addUnderLabel(LABEL_PARENTS, subjectItem, newNode);
                        }
                    }
                    else if(type.equals(ROLE_MOTHER))
                    {
                        Person mother = subjectPerson.getMother();
                        if(mother != null)
                        {
                            replace = new Alert(AlertType.CONFIRMATION, confirm+mother.getFName()+" "+mother.getSurname()+" as mother?", arr);

                            replace.showAndWait().ifPresent(response -> 
                            {
                                if (response == ButtonType.YES)
                                {
                                    removeNode(subjectItem, ROLE_MOTHER);
                                    subjectPerson.setMother(newPerson);
                                    addUnderLabel(LABEL_PARENTS, subjectItem, newNode);
                                } 
                            });
                        }
                        else
                        {
                            subjectPerson.setMother(newPerson);
                            addUnderLabel(LABEL_PARENTS, subjectItem, newNode);
                        }
                    }
                    else if(type.equals(ROLE_SPOUSE))
                    {
                        Person spouse = subjectPerson.getSpouse();
                        if(spouse != null)
                        {
                            replace = new Alert(AlertType.CONFIRMATION, confirm+spouse.getFName()+" "+spouse.getSurname()+" as spouse?", arr);

                            replace.showAndWait().ifPresent(response -> 
                            {
                                if (response == ButtonType.YES)
                                {
                                    removeNode(subjectItem, ROLE_SPOUSE);
                                    subjectPerson.setSpouse(newPerson);
                                    addUnderLabel(LABEL_SPOUSE, subjectItem, newNode);
                                } 
                            });
                        }
                        else
                        {
                            subjectPerson.setSpouse(newPerson);
                            addUnderLabel(LABEL_SPOUSE, subjectItem, newNode);
                        }
                    }
                    else if(type.equals(ROLE_CHILD))
                    {
                        TreeItem<Object> parent = subjectItem.getParent();
                        if(parent == null)
                            addUnderLabel(LABEL_CHILDREN, subjectItem, newNode);
                        else if(parent.getValue().equals(LABEL_SPOUSE))
                        {
                            TreeItem<Object> src = parent.getParent();
                            addUnderLabel(LABEL_CHILDREN, src, newNode);
                        }
                        else if(parent.getValue().equals(LABEL_PARENTS))
                        {
                            TreeItem<Object> src = parent.getParent();
                            addUnderLabel(LABEL_SIBLINGS, src, newNode);
                        }
                        else
                            addUnderLabel(LABEL_CHILDREN, subjectItem, newNode);
                        
                        if(subjectPerson.getGender() == 'M')
                            newPerson.setFather(subjectPerson);
                        else
                            newPerson.setMother(subjectPerson);
                    }
                    updateInfoPane(subjectItem);
                    saveNeeded = true;
                    addView.close();
                }
                else
                {
                    int end;
                    int i = 0;
                    while(i < msg.length())
                    {
                        end = msg.indexOf("\n", i);
                        if(end != -1)
                        {
                            Label lb = new Label(msg.substring(i, end+1));
                            lb.setTextFill(Color.web("#ff0000"));
                            err.getChildren().add(lb);
                            i = end+1;
                        }
                    }
                }
            });
        }
        else // if add root
        {
            submit.setOnAction(e -> 
            {
                err.getChildren().clear();
                LinkedHashMap<String, String> input = new LinkedHashMap<>();

                input.put("first name", fName.getText());
                input.put("surname", surname.getText());

                if(gender.getValue() == null)
                    input.put("gender", "");
                else
                    input.put("gender", gender.getValue());

                input.put("maiden name", maidenName.getText());
                input.put("life description", desc.getText());
                input.put("street number", streetNo.getText());
                input.put("street name", streetName.getText());
                input.put("suburb name", suburbName.getText());
                input.put("postal code", postalCode.getText());

                String msg = validate(input);
                if(msg.isEmpty())
                {
                    int stNo = Integer.parseInt(streetNo.getText());
                    char g = gender.getValue().equals("Male") ? 'M' : 'F';
                    Node image = g == 'M' ? new ImageView(male) : new ImageView(female); 
                    counter++;
                    String id = Integer.toString(counter);
                    Address addr = new Address(stNo, streetName.getText(), suburbName.getText(), postalCode.getText());
                    Person newPerson = new Person(id, fName.getText(), surname.getText(), maidenName.getText(), g, addr, desc.getText());
                    TreeItem<Object> root = new TreeItem<>(newPerson, image);
                    createTree(root);
                    saveNeeded = true;
                    addView.close();
                }
                else
                {
                    int end;
                    int i = 0;
                    while(i < msg.length())
                    {
                        end = msg.indexOf("\n", i);
                        if(end != -1)
                        {
                            Label lb = new Label(msg.substring(i, end+1));
                            lb.setTextFill(Color.web("#ff0000"));
                            err.getChildren().add(lb);
                            i = end+1;
                        }
                    }
                }
            });
        }
        
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> {addView.close();});
        
        HBox buttons = new HBox();
        
        if(addRoot)
            buttons.getChildren().addAll(submit, cancel);
        else
        {
            Button addr = new Button("Set the same address");
            HBox.setMargin(addr, new Insets(10,10,5,10));
            buttons.getChildren().addAll(submit, addr, cancel);
            addr.setOnAction(e -> {
                Person subjectPerson = (Person)subjectItem.getValue();
                streetNo.setText(Integer.toString(subjectPerson.getAddr().getStNum()));
                streetName.setText(subjectPerson.getAddr().getStName());
                suburbName.setText(subjectPerson.getAddr().getSuburb());
                postalCode.setText(subjectPerson.getAddr().getPostcode());
            });
        }
        
        buttons.setAlignment(Pos.CENTER);
        HBox.setMargin(submit, new Insets(10,10,5,10));
        HBox.setMargin(cancel, new Insets(10,10,5,10));
        
        vb.getChildren().addAll(fName_label, fName, surname_label, surname, maiden_label, maidenName, gender_label, gender);
        setFieldMargin(fName, surname, maidenName);
        setLabelMargin(fName_label, surname_label, maiden_label, gender_label);
        VBox.setMargin(gender, new Insets(0,10,15,10));
        
        vb.getChildren().addAll(desc_label, desc, no_label, streetNo, name_label, streetName, suburb_label, suburbName, postal_label, postalCode);
        setLabelMargin(desc_label, no_label, name_label, suburb_label, postal_label);
        setFieldMargin(desc, streetNo, streetName, suburbName);
        VBox.setMargin(postalCode, new Insets(0,10,5,10));
        
        vb.getChildren().addAll(buttons);
        
        ScrollPane main = new ScrollPane(vb);
        
        addView.setScene(new Scene(main));
        
        if(addRoot)
            addView.setTitle("Add a root");
        else
            addView.setTitle("Add a relative");
        addView.initModality(Modality.APPLICATION_MODAL);
        addView.setHeight(600);
        addView.show();
    }
    
    /**
     * Remove father/mother/spouse of a person in a TreeView
     * @param subjectItem
     * @param role 
     */
    private void removeNode(TreeItem<Object> subjectItem, String role)
    {
        String lb = "";
        if(role.equals(ROLE_FATHER) || role.equals(ROLE_MOTHER))
            lb = LABEL_PARENTS;
        else if(role.equals(ROLE_SPOUSE))
            lb = LABEL_SPOUSE;
        else if(role.equals(ROLE_CHILD))
            lb = LABEL_CHILDREN;
        
        if( !(subjectItem.getValue() instanceof Person))
            return;
        
        Person subjectPerson = (Person)subjectItem.getValue();
        
        for(TreeItem<Object> item : subjectItem.getChildren())
        {
            Object current = item.getValue();
            if(current instanceof String)
            {
                if(current.equals(lb))
                {
                    TreeItem<Object> holder = null;
                    for(TreeItem<Object> item2 : item.getChildren())
                    {
                        if(item2.getValue() instanceof Person)
                        {
                            Person checkPerson = (Person)item2.getValue();
                            boolean s1 = role.equals(ROLE_FATHER) && checkPerson.equals(subjectPerson.getFather());
                            boolean s2 = role.equals(ROLE_MOTHER) && checkPerson.equals(subjectPerson.getMother());
                            boolean s3 = role.equals(ROLE_SPOUSE) && checkPerson.equals(subjectPerson.getSpouse());
                            boolean s4 = role.equals(ROLE_CHILD) && subjectPerson.getChildren().contains(checkPerson);
                            if(s1 || s2 || s3 || s4)
                            {
                                holder = item2;
                                break;
                            }
                        }
                    }
                    if(holder != null)
                    {
                        item.getChildren().remove(holder);
                        if(item.getChildren().isEmpty())
                            subjectItem.getChildren().remove(item);
                        break;
                    }
                }
           }
        }
    }
    
    /**
     * Edit details of a member
     * @param item - Node of member
     */
    private void editDetails(TreeItem<Object> item)
    {
        Stage editView = new Stage();
        
        Person person = (Person)item.getValue();
        
        VBox vb = new VBox();
        VBox err = new VBox();
        HBox err_box = new HBox();
        err_box.getChildren().add(err);
        err_box.setAlignment(Pos.CENTER);
        
        vb.setPadding(new Insets(20,20,20,20));
        
        Label instruction = new Label("Edit the details for "+person.getFName()+" "+person.getSurname()+" : ");
        
        instruction.setStyle("-fx-font-size:20");
        
        Label info = new Label("(*) means mandatory field.");
        
        vb.getChildren().addAll(instruction, info, err_box);
        VBox.setMargin(instruction, new Insets(5,20,5,20));
        VBox.setMargin(info, new Insets(5,20,10,20));
        VBox.setMargin(err, new Insets(0,0,10,0));
        
        Label fName_label = new Label("First Name* : ");
        TextField fName = new TextField(person.getFName());
        
        Label surname_label = new Label("Surname : ");
        TextField surname = new TextField(person.getSurname());
        
        Label maiden_label = new Label("Maiden name : ");
        TextField maidenName = new TextField(person.getMaidenName());
        
        Label gender_label = new Label("Gender* : ");
        ChoiceBox<String> gender = new ChoiceBox<>();
        gender.getItems().addAll("Male", "Female");
        if(person.getGender() == 'M')
            gender.setValue("Male");
        else
            gender.setValue("Female");
        if(person.getSpouse() != null || !person.getChildren().isEmpty()) // If a spouse or a father/mother, cannot edit gender
            gender.setDisable(true);
  
        Label desc_label = new Label("Life description : ");
        TextArea desc = new TextArea(person.getDesc());
        desc.setPrefRowCount(3);
        desc.setPrefColumnCount(3);
        
        Label no_label = new Label("Street no.* : ");
        TextField streetNo = new TextField(Integer.toString(person.getAddr().getStNum()));
        
        Label name_label = new Label("Street name* : ");
        TextField streetName = new TextField(person.getAddr().getStName());
        
        Label suburb_label = new Label("Suburb name* : ");
        TextField suburbName = new TextField(person.getAddr().getSuburb());
        
        Label postal_label = new Label("Postal code* : ");
        TextField postalCode = new TextField(person.getAddr().getPostcode());
        
        Button submit = new Button("Submit");
        submit.setOnAction(e -> 
        {
            err.getChildren().clear();
            LinkedHashMap<String, String> input = new LinkedHashMap<String, String>();

            input.put("first name", fName.getText());
            input.put("surname", surname.getText());

            if(gender.getValue() == null)
                input.put("gender", "");
            else
                input.put("gender", gender.getValue());

            input.put("maiden name", maidenName.getText());
            input.put("life description", desc.getText());
            input.put("street number", streetNo.getText());
            input.put("street name", streetName.getText());
            input.put("suburb name", suburbName.getText());
            input.put("postal code", postalCode.getText());

            String msg = validate(input);
            if(msg.isEmpty())
            {
                int stNo = Integer.parseInt(streetNo.getText());
                char g = gender.getValue().equals("Male") ? 'M' : 'F';
                Node image = g == 'M' ? new ImageView(male) : new ImageView(female); 
                String id = Integer.toString(counter+1);
                Address addr = new Address(stNo, streetName.getText(), suburbName.getText(), postalCode.getText());
                
                person.setFName(fName.getText());
                person.setSurname(surname.getText());
                person.setMaidenName(maidenName.getText());
                person.setGender(g);
                person.setAddr(addr);
                person.setDesc(desc.getText());
                item.setGraphic(image);
                updateInfoPane(item);
                saveNeeded = true;
                editView.close();
            }
            else
            {
                int end;
                int i = 0;
                while(i < msg.length())
                {
                    end = msg.indexOf("\n", i);
                    if(end != -1)
                    {
                        Label lb = new Label(msg.substring(i, end+1));
                        lb.setTextFill(Color.web("#ff0000"));
                        err.getChildren().add(lb);
                        i = end+1;
                    }
                }
            }
        });
        
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> {editView.close();});
        
        HBox buttons = new HBox();
        buttons.getChildren().addAll(submit, cancel);
        
        buttons.setAlignment(Pos.CENTER);
        HBox.setMargin(submit, new Insets(10,10,5,10));
        HBox.setMargin(cancel, new Insets(10,10,5,10));
        
        vb.getChildren().addAll(fName_label, fName, surname_label, surname, maiden_label, maidenName, gender_label, gender);
        setFieldMargin(fName, surname, maidenName);
        setLabelMargin(fName_label, surname_label, maiden_label, gender_label);
        VBox.setMargin(gender, new Insets(0,10,15,10));
        
        vb.getChildren().addAll(desc_label, desc, no_label, streetNo, name_label, streetName, suburb_label, suburbName, postal_label, postalCode);
        setLabelMargin(desc_label, no_label, name_label, suburb_label, postal_label);
        setFieldMargin(desc, streetNo, streetName, suburbName);
        VBox.setMargin(postalCode, new Insets(0,10,5,10));
        
        vb.getChildren().addAll(buttons);
        
        ScrollPane main = new ScrollPane(vb);
        
        editView.setScene(new Scene(main));
        
        editView.setTitle("Edit a person's data");
        editView.initModality(Modality.APPLICATION_MODAL);
        editView.show();
    }
    
    /**
     * Validate whether the mandatory inputs are supplied, names are alphabets or spaces only, 
     * as well as an integer street number
     * @param input map to validate
     * @return error message, if any
     */
    private String validate(LinkedHashMap<String, String> input)
    {
        boolean nameFalse = false;
        String msg = "";
        Iterator<Map.Entry<String, String>> it = input.entrySet().iterator();
        ArrayList<String> non_mandatory = new ArrayList<>();
        non_mandatory.add("life description");
        non_mandatory.add("surname");
        non_mandatory.add("maiden name");
        while (it.hasNext()) 
        {
            Map.Entry<String, String> entry = it.next();
            String text = entry.getKey();
            if(!non_mandatory.contains(text))
            {
                if(entry.getValue().isEmpty() || entry.getValue().trim().length() == 0)
                {
                    msg += "Input "+entry.getKey()+"\n";
                }
                else if(text.equals("street number"))
                {
                    try
                    {
                        Integer.parseInt(entry.getValue());
                    }
                    catch(NumberFormatException e)
                    {
                        msg += "Input street number as integer\n";
                    }
                }
                
            }
            if((text.equals("first name") || text.equals("surname") || text.equals("maiden name") ) && !nameFalse)
            {
                String input_str = entry.getValue().trim();
                
                for (int i = 0; i < input_str.length(); i++) 
                {
                    if(!Character.isLetter(input_str.charAt(i)) && input_str.charAt(i) != ' ') 
                    {
                        msg += "Names consist of only alphabets and spaces\n";
                        nameFalse = true;
                        break;
                    }    
                }
            }
            it.remove();
        }
        return msg;
    }
    
    /**
     * Show a dialog to open a file. Has two extension filters : .dat or *.*
     * @param topView - Stage where dialog is displayed
     */
    private void loadTreeFile(Stage topView)
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Family Tree");
        
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Binary file", "*.dat"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fc.showOpenDialog(topView);
        if(file != null)
        {
            if(saveNeeded)
            {
                ButtonType b1 = new ButtonType("Save");
                ButtonType[] arr = new ButtonType[3];
                arr[0] = b1;
                arr[1] = ButtonType.NO;
                arr[2] = ButtonType.CANCEL;
                Alert saveChanges = new Alert(AlertType.CONFIRMATION, "Do you want to save changes before loading another file?", arr);
                
                saveChanges.showAndWait().ifPresent(response -> 
                {
                    if (response == ButtonType.NO) 
                        saveNeeded = false;
                    if (response == b1)
                    {
                        saveTreeFile(topView);
                        saveNeeded = false;
                    } 
                });
            }
            
            if(saveNeeded) // Cancel will cancel loading the file. 
                return;
            
            if(app.loadFile(file) == 0)
            {
                filename_text.setText(file.getName());
                if(app.getRoot() != null)
                    loadTree(app.getRoot());
                else
                {
                    initTreePane();
                    initInfoPane();
                }
            }
            else
            {
                Alert error = new Alert(AlertType.ERROR, "File cannot be loaded.");
                error.showAndWait();
            }
        }
    }
    
    /**
     * Show a dialog to save a file. Has two extension filters : .dat and *.*
     * @param topView - Stage where dialog is displayed
     */
    private void saveTreeFile(Stage topView)
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Family Tree");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Binary file", "*.dat"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fc.showSaveDialog(topView);
        if(file != null)
        {
            filename_text.setText(file.getName());
            if(tree.getRoot() != null)
                app.setRoot((Person)tree.getRoot().getValue());
            else
                app.setRoot(null);
            app.saveFile(file);
            if(app.saveFile(file) != 0)
            {
                Alert error = new Alert(AlertType.ERROR, "File cannot be saved.");
                error.showAndWait();
            }
            else
                saveNeeded = false;
        }
    }
    
    /**
     * Set the root of the tree
     * @param root - Node that becomes the root
     */
    private void createTree(TreeItem<Object> root)
    {
        root.setExpanded(true);
        tree.setRoot(root);
        updateInfoPane(root);
    }
    
    /**
     * Initialize empty tree
     */
    private void initTree()
    {
        tree = new TreeView<>();
        tree.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {      
                try
                {
                    TreeItem<Object> p = tree.getSelectionModel().getSelectedItem();
                    if(p.getValue() instanceof Person)
                        updateInfoPane(p);
                }
                catch(NullPointerException e)
                {}
            }
        });
    }
    
    /**
     * Load tree from a single person
     * @param person - root, if null means empty tree
     */
    private void loadTree(Person person)
    {
        if(person != null)
            addNode(null, person);
        else
        {
            initTreePane();
            initInfoPane();
        }
    }
    
    /**
     * Add under Parents, Spouse, Child, or Siblings
     * @param label
     * @param parentItem
     * @param childItem 
     */
    private void addUnderLabel(String label, TreeItem<Object> parentItem, TreeItem<Object> childItem)
    {
        for(TreeItem<Object> item : parentItem.getChildren())
        {
            Object lb = item.getValue();
            if(lb instanceof String)
            {
                if(label.equals(lb))
                {
                    item.getChildren().add(childItem);
                    return;
                }
           }
        }
        TreeItem<Object> lb = new TreeItem<>(label);
        parentItem.getChildren().add(lb);
        lb.getChildren().add(childItem);
        lb.setExpanded(true);
        parentItem.setExpanded(true);
        parentItem.getChildren().sort(compareNode);
    }
    
    /**
     * Recursive function
     * @param parentItem - Parent of new node
     * @param newPerson - Person who will be the new node
     */
    private void addNode(TreeItem<Object> parentItem, Person newPerson)
    {
        Node image = newPerson.getGender() == 'M' ? new ImageView(male) : new ImageView(female);
        TreeItem<Object> subjectItem = new TreeItem<>(newPerson, image);
        
        if(parentItem == null)
        {
            createTree(subjectItem);
            Person f = newPerson.getFather();
            Person m = newPerson.getMother();
            Person s = newPerson.getSpouse();
            ArrayList<Person> c = newPerson.getChildren();
            ArrayList<Person> sb = new ArrayList<>();
            
            if(f != null)
            {
                addNode(subjectItem, f);
                sb = f.getChildren();
            }
            if(m != null)
            {
                addNode(subjectItem, m);
                for(Person ch : m.getChildren())
                {
                    if(!sb.contains(ch))
                        sb.add(ch);
                }
            }
            if(s != null)
                addNode(subjectItem, s);
            
            if(!c.isEmpty())
            {
                for(Person child : c)
                {
                    addNode(subjectItem, child);
                }
            }
            if(!sb.isEmpty())
            {
                for(Person sibling : sb)
                {
                    if(!sibling.equals(newPerson))
                        addNode(subjectItem, sibling);
                }
            }
        }
        else
        {
            TreeItem<Object> rootItem = tree.getRoot();
            Person rootPerson = (Person)tree.getRoot().getValue();
            Person parentPerson = (Person)parentItem.getValue();
            ArrayList<Person> childArray = parentPerson.getChildren();
            ArrayList<Person> sibling = new ArrayList<>();
            Person father = rootPerson.getFather();
            Person mother = rootPerson.getMother();
            
            if(father != null)
                sibling = father.getChildren();
            
            if(mother != null)
            {
                for(Person mchild : mother.getChildren())
                {
                    if(!sibling.contains(mchild))
                        sibling.add(mchild);
                }
            }
            
            if(newPerson.equals(father) || newPerson.equals(mother))
            {
                addUnderLabel(LABEL_PARENTS, rootItem, subjectItem);
            }
            else if(newPerson.equals(parentPerson.getSpouse()))
            {
                addUnderLabel(LABEL_SPOUSE, parentItem, subjectItem);
            }
            else 
            {
                if(childArray.contains(newPerson))
                    addUnderLabel(LABEL_CHILDREN, parentItem, subjectItem);
                else if(sibling.contains(newPerson))
                    addUnderLabel(LABEL_SIBLINGS, parentItem, subjectItem);
                Person sp = newPerson.getSpouse();
                if(sp!= null)
                    addNode(subjectItem, sp);
                for(Person child : newPerson.getChildren())
                {
                    addNode(subjectItem, child);
                }
            }
        }
    }
    
    /**
     * Show person's information of a highlighted tree item
     * @param item - highlighted node
     */
    private void updateInfoPane(TreeItem<Object> item)
    {
        clearInfoPane();
      
        Person person = (Person)item.getValue(); 
        
        HBox buttons = new HBox();
        Button editDetails = new Button("Edit Details");
        Button addRelative = new Button(ADD_RELATIVE);
        HBox.setMargin(editDetails, (new Insets(10,10,10,10)));
        HBox.setMargin(addRelative, (new Insets(10,10,10,10)));
        addRelative.setOnAction(e -> addMember(item));
        editDetails.setOnAction(e -> editDetails(item));
        
        buttons.getChildren().addAll(editDetails, addRelative);
        
        GridPane person_table = new GridPane();
        GridPane addr_table = new GridPane();
        GridPane relatives_table = new GridPane();
        
        // Add PERSON INFO
        Label person_info = new Label("PERSON INFO     ");
       
        Label fName = new Label("First name: ");
        Label name = new Label(person.getFName());
        
        Label surname = new Label("Surname: ");
        Label surname_input;
        
        if(person.getSurname().isEmpty())
            surname_input = new Label("-");
        else
            surname_input = new Label(person.getSurname());
        
        Label gender = new Label("Gender: ");
        String g = "Male";
        if(person.getGender() == 'F')
            g = "Female";
        Label gender_input = new Label(g);
        
        Label maidenName = new Label("Maiden name: ");
        Label maidenName_input;
        if(person.getMaidenName().isEmpty())
            maidenName_input = new Label("-");
        else
            maidenName_input = new Label(person.getMaidenName());
        
        String desc_text = "Life description: ";
        Label desc = new Label(desc_text);
        
        if(person.getDesc().isEmpty())
            person_table.addRow(5, desc, new Label("-"));
        else
        {
            TextArea desc_area = new TextArea(person.getDesc());
            person_table.addRow(5, desc, desc_area);
            desc_area.setEditable(false);
            desc_area.setPrefColumnCount(6);
        }
        
        person_table.addRow(0, person_info);
        person_table.addRow(1, fName, name);
        person_table.addRow(2, surname, surname_input);
        person_table.addRow(3, gender, gender_input);
        person_table.addRow(4, maidenName, maidenName_input);
        
        info_layout.getChildren().addAll(buttons, person_table, addr_table, relatives_table);
        VBox.setMargin(person_table, new Insets(10,10,10,10));
        VBox.setMargin(addr_table, new Insets(10,10,10,10));
        VBox.setMargin(relatives_table, new Insets(10,10,10,10));
        
        // Add ADDRESS INFO
        Label addr_info = new Label("ADDRESS INFO    ");
        Label streetNo = new Label("Street no.: ");
        Label stNo_input = new Label(Integer.toString(person.getAddr().getStNum()));
        Label streetName = new Label("Street name: ");
        Label stName_input = new Label(person.getAddr().getStName());
        Label suburbName = new Label("Suburb name: ");
        Label suburbName_input = new Label(person.getAddr().getSuburb());
        
        addr_table.addRow(0, addr_info);
        addr_table.addRow(1, streetNo, stNo_input);
        addr_table.addRow(2, streetName, stName_input);
        addr_table.addRow(3, suburbName, suburbName_input);
       
        // Add RELATIVE INFO
        Label relative_info = new Label("RELATIVE(S) INFO");
        Person f = person.getFather();
        Person m = person.getMother();
        Person s = person.getSpouse();
        ArrayList<Person> children = person.getChildren();
        
        Label father = new Label("Father: ");
        Label father_name = new Label("-");
        if(f != null)
            father_name.setText(f.getFName()+" "+f.getSurname());
        
        Label mother = new Label("Mother: ");
        Label mother_name = new Label("-");
        if(m != null)
            mother_name.setText(m.getFName()+" "+m.getSurname());
        
        Label spouse = new Label("Spouse: ");
        Label spouse_name = new Label("-");
        if(s != null)
            spouse_name.setText(s.getFName()+" "+s.getSurname());
        
        Label child = new Label("Children: ");
        Label child_name = new Label("-");
        
        Label grandchildren = new Label("Grand Children: ");
        Label grandchildren_name = new Label("-");
        
        int rowg = 5;
        if(children.size() > 1)
            rowg = 4 + children.size();
        
        int grandchildren_count = 0;
        
        for(int i = 0; i < children.size(); i++)
        {
            Person child_idx = children.get(i);
            String fullName = "> "+child_idx.getFName()+" "+child_idx.getSurname();
            if(i == 0)
                child_name.setText(fullName);
            else
            {
                Label child_other = new Label(fullName);
                relatives_table.add(child_other, 1, 4+i);
            }
            
            ArrayList<Person> grandchildren_arr = child_idx.getChildren();
            for(int n = 0; n < grandchildren_arr.size(); n++)
            {
                Person gchild_idx = grandchildren_arr.get(n);
                String gfullName = "> "+gchild_idx.getFName()+" "+gchild_idx.getSurname();
                
                if(grandchildren_count == 0)
                    grandchildren_name.setText(gfullName);
                else
                    relatives_table.add(new Label(gfullName), 1, rowg+grandchildren_count);
                grandchildren_count++;
            }
        }
        
        relatives_table.addRow(0, relative_info);
        relatives_table.addRow(1, father, father_name);
        relatives_table.addRow(2, mother, mother_name);
        relatives_table.addRow(3, spouse, spouse_name);
        relatives_table.addRow(4, child, child_name);
        relatives_table.addRow(rowg, grandchildren, grandchildren_name);
        
        setLabelStyle(fName, surname, gender, maidenName, desc, streetNo, streetName, suburbName, father, mother, spouse, child, grandchildren);
        setCategoryStyle(person_info, addr_info, relative_info);
    }
    
    /**
     * Set CSS style for a group of labels
     * @param nodes 
     */
    private void setCategoryStyle(Node... nodes)
    {
        for(Node node : nodes)
        {
            node.setStyle("-fx-font-size:15; -fx-font-weight:bold");
        }
    }
    
    /**
     * Set label margin
     * @param nodes 
     */
    private void setLabelMargin(Node... nodes)
    {
        for(Node node : nodes)
        {
            VBox.setMargin(node, new Insets(0,0,0,10));
        }
    }
    
    /**
     * Set field margin
     * @param nodes 
     */
    private void setFieldMargin(Node... nodes)
    {
        for(Node node : nodes)
        {
            VBox.setMargin(node, new Insets(0,10,10,10));
        }
    }
}

    

