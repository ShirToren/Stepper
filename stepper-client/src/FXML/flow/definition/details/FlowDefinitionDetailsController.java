package FXML.flow.definition.details;

import FXML.main.MainAppController;
import impl.DataInFlowDTO;
import impl.FlowDefinitionDTO;
import impl.StepUsageDeclarationDTO;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class FlowDefinitionDetailsController {
    private MainAppController mainAppController;
    @FXML
    private BorderPane borderPane;
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
        //borderPane.getStylesheets().add("/FXML/css/second.css");
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

                String finalUrl = HttpUrl
                        .parse(Constants.FLOW_DEFINITION)
                        .newBuilder()
                        .addQueryParameter("flowName", currentFlowName)
                        .build()
                        .toString();

                HttpClientUtil.runAsync(finalUrl, new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
/*                Platform.runLater(() ->
                                showErrorDialog("Error", "Something went wrong: " + e.getMessage())
                        //errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );*/
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String jsonFlowDefinition = response.body().string();
                        if (response.isSuccessful()) {
                            //String jsonFlowDefinition = response.body().string();
                            FlowDefinitionDTO flowDefinitionDTO = GSON_INSTANCE.fromJson(jsonFlowDefinition, FlowDefinitionDTO.class);
                            Platform.runLater(() -> {
                                for (DataInFlowDTO input: flowDefinitionDTO.getFlowsInputs()) {
                                    if(input.getOwnerStep().getName().equals(item)) {
                                        createStepInputsTreeView(input, inputsList);
                                    }
                                }
                                for (DataInFlowDTO output: flowDefinitionDTO.getFlowsOutputs()) {
                                    if(output.getOwnerStep().getName().equals(item)) {
                                        createStepOutputsTreeView(output,outputsList);
                                    }
                                }
                            });
                        }
                    }
                });
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
        TreeItem<String> child2 = new TreeItem<>(input.getDataNecessity().toLowerCase());
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

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_DEFINITION)
                .newBuilder()
                .addQueryParameter("flowName", flowName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
/*                Platform.runLater(() ->
                                showErrorDialog("Error", "Something went wrong: " + e.getMessage())
                        //errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );*/
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
/*                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showErrorDialog("Error", "Something went wrong: " + responseBody));*/
                } else {
                    String jsonFlowDefinition = response.body().string();
                    FlowDefinitionDTO flowDefinitionDTO = GSON_INSTANCE.fromJson(jsonFlowDefinition, FlowDefinitionDTO.class);
                    Platform.runLater(() -> {
                        // FlowDefinitionDTO dto = mainAppController.getModel().showFlowDefinition(flowName);
                        nameLabel.setText(flowDefinitionDTO.getName());
                        descriptionLabel.setText(flowDefinitionDTO.getDescription());
                        if(flowDefinitionDTO.isReadOnly()) {
                            readOnlyLabel.setText("True");
                        } else {
                            readOnlyLabel.setText("False");
                        }
                        for (DataInFlowDTO output: flowDefinitionDTO.getFlowsFormalOutputs()) {
                            formalOutputsLVItems.add(output.getFinalName());
                        }
                        for (DataInFlowDTO output: flowDefinitionDTO.getFlowsOutputs()) {
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

                        for (DataInFlowDTO input: flowDefinitionDTO.getFreeInputs()) {
                            TreeItem<String> rootItem = new TreeItem<>("");
                            freeInputsTV.setRoot(rootItem);
                            TreeItem<String> child1 = new TreeItem<>(input.getFinalName());
                            freeInputsList.add(child1);
                            rootItem.getChildren().addAll(freeInputsList);
                            TreeItem<String> child2 = new TreeItem<>(input.getDataDefinition().getName());
                            TreeItem<String> child3 = new TreeItem<>(input.getDataNecessity().toLowerCase());
                            TreeItem<String> child4 = new TreeItem<>("Related Steps");
                            child1.getChildren().addAll(child2,child3,child4);
                            for (String step: flowDefinitionDTO.getFreeInputsStepTarget().get(input.getFinalName())) {
                                relatedStepsList.add(new TreeItem<>(step));
                            }
                            child4.getChildren().addAll(relatedStepsList);
                            relatedStepsList.clear();


                            // Expand the root item to show the children
                            rootItem.setExpanded(true);
                        }

                        for (StepUsageDeclarationDTO step: flowDefinitionDTO.getSteps()) {
                            stepsLVItems.add(step.getName());
                        }
                    });
                }
            }
        });
    }
    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

}