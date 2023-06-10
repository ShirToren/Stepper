package FXML.flow.definition.details;

import FXML.main.MainAppController;
import dto.DataInFlowDTO;
import dto.FlowDefinitionDTO;
import dto.StepUsageDeclarationDTO;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class FlowDefinitionDetailsController {
    private MainAppController mainAppController;
    private String currentFlowName;
    @FXML private Button executeButton;
    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ListView<String> formalOutputsLV;
    private final ObservableList<String> formalOutputsLVItems = FXCollections.observableArrayList();
    private final ObservableList<String> stepsLVItems = FXCollections.observableArrayList();

    @FXML
    private Label readOnlyLabel;

    @FXML
    private TreeView<String> freeInputsTV;

    @FXML
    private TreeView<String> allOutputsTV;
    @FXML
    private ListView<String> stepsLV;
    @FXML
    private TreeView<String> stepInputsTV;

    @FXML
    private TreeView<String> stepOutputsTV;


    @FXML
    public void initialize() {
        descriptionLabel.setWrapText(true);
        formalOutputsLV.setItems(formalOutputsLVItems);
        stepsLV.setItems(stepsLVItems);
    }

    public void clearPrevDetails() {
        formalOutputsLVItems.clear();
        stepsLVItems.clear();
        nameLabel.setText("");
        descriptionLabel.setText("");
        readOnlyLabel.setText("");
        if(freeInputsTV.getRoot() != null && allOutputsTV.getRoot() != null) {
            freeInputsTV.getRoot().getChildren().clear();
            allOutputsTV.getRoot().getChildren().clear();
        }
        if(stepInputsTV.getRoot() != null && stepOutputsTV.getRoot() != null){
            stepInputsTV.getRoot().getChildren().clear();
            stepOutputsTV.getRoot().getChildren().clear();
        }
    }

    @FXML
    void rowClickedActionListener(MouseEvent event) {
        List<TreeItem<String>> inputsList = new ArrayList<>();
        List<TreeItem<String>> outputsList = new ArrayList<>();
        if(event.getClickCount() == 1) {

            int selectedIndex = stepsLV.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < stepsLVItems.size()) {
                String item = stepsLVItems.get(selectedIndex);
                FlowDefinitionDTO dto = mainAppController.getModel().showFlowDefinition(currentFlowName);
                for (DataInFlowDTO input: dto.getFlowsInputs()) {
                    if(input.getOwnerStep().getName().equals(item)) {
                        createStepInputsTreeView(input, inputsList);
                    }
                }
                for (DataInFlowDTO output: dto.getFlowsOutputs()) {
                    if(output.getOwnerStep().getName().equals(item)) {
                        createStepOutputsTreeView(output,outputsList);
                    }
                }

            } else {
                // Invalid index
                System.out.println("Invalid index.");
            }

        }
    }

    private void createStepOutputsTreeView(DataInFlowDTO output, List<TreeItem<String>> outputsList) {
        TreeItem<String> rootItem = new TreeItem<>("");
        stepOutputsTV.setRoot(rootItem);
        // Add child TreeItems to the root
        TreeItem<String> child1 = new TreeItem<>(output.getFinalName());
        outputsList.add(child1);
        rootItem.getChildren().addAll(outputsList);
        TreeItem<String> child3;
        if(output.getTargetSteps().size() > 0) {
            child3 = new TreeItem<>("Target input: " + output.getTargetSteps().get(0).getName());
        } else {
            child3 = new TreeItem<>("No target input");
        }


        child1.getChildren().addAll(child3);

        // Expand the root item to show the children
        rootItem.setExpanded(true);
    }

    private void createStepInputsTreeView(DataInFlowDTO input, List<TreeItem<String>> inputsList) {
        TreeItem<String> rootItem = new TreeItem<>("");
        stepInputsTV.setRoot(rootItem);
        // Add child TreeItems to the root
        TreeItem<String> child1 = new TreeItem<>(input.getFinalName());
        inputsList.add(child1);
        rootItem.getChildren().addAll(inputsList);
        TreeItem<String> child2 = new TreeItem<>(input.getDataNecessity().name().toLowerCase());
        TreeItem<String> child3;
        if(input.getSourceSteps().size() > 0) {
            child3 = new TreeItem<>("Source output: " + input.getSourceSteps().get(0).getName());
        } else {
            child3 = new TreeItem<>("No source output");
        }


        child1.getChildren().addAll(child2,child3);

        // Expand the root item to show the children
        rootItem.setExpanded(true);
    }

    @FXML
    void executeButtonActionListener(ActionEvent event) {
        mainAppController.executeFlowButtonActionListener();
    }

    public BooleanProperty getExecuteButtonDisableProperty(){
        return  executeButton.disableProperty();
    }


    public void addFlowDetails(String flowName){
        currentFlowName = flowName;
        List<TreeItem<String>> outputsList = new ArrayList<>();
        List<TreeItem<String>> freeInputsList = new ArrayList<>();
        List<TreeItem<String>> relatedStepsList = new ArrayList<>();
        FlowDefinitionDTO dto = mainAppController.getModel().showFlowDefinition(flowName);
        nameLabel.setText(dto.getName());
        descriptionLabel.setText(dto.getDescription());
        if(dto.isReadOnly()) {
            readOnlyLabel.setText("True");
        } else {
            readOnlyLabel.setText("False");
        }
        for (DataInFlowDTO output: dto.getFlowsFormalOutputs()) {
            formalOutputsLVItems.add(output.getFinalName());
        }
        for (DataInFlowDTO output: dto.getFlowsOutputs()) {
            TreeItem<String> rootItem = new TreeItem<>("");
            allOutputsTV.setRoot(rootItem);
            // Add child TreeItems to the root
            TreeItem<String> child1 = new TreeItem<>(output.getFinalName());
            outputsList.add(child1);
            rootItem.getChildren().addAll(outputsList);
            TreeItem<String> child2 = new TreeItem<>(output.getDataDefinition().getName());
            TreeItem<String> child3 = new TreeItem<>("From Step: " + output.getOwnerStep().getName());
            child1.getChildren().addAll(child2,child3);

            // Expand the root item to show the children
            rootItem.setExpanded(true);
        }

        for (DataInFlowDTO input: dto.getFreeInputs()) {
            TreeItem<String> rootItem = new TreeItem<>("");
            freeInputsTV.setRoot(rootItem);
            TreeItem<String> child1 = new TreeItem<>(input.getFinalName());
            freeInputsList.add(child1);
            rootItem.getChildren().addAll(freeInputsList);
            TreeItem<String> child2 = new TreeItem<>(input.getDataDefinition().getName());
            TreeItem<String> child3 = new TreeItem<>(input.getDataNecessity().name().toLowerCase());
            TreeItem<String> child4 = new TreeItem<>("Related Steps");
            child1.getChildren().addAll(child2,child3,child4);
            for (String step: dto.getFreeInputsStepTarget().get(input.getFinalName())) {
                relatedStepsList.add(new TreeItem<>(step));
            }
            child4.getChildren().addAll(relatedStepsList);
            relatedStepsList.clear();


            // Expand the root item to show the children
            rootItem.setExpanded(true);
        }

        for (StepUsageDeclarationDTO step: dto.getSteps()) {
            stepsLVItems.add(step.getName());
        }

    }



    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

}