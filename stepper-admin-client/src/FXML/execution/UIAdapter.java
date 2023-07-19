package FXML.execution;

import impl.DataInFlowDTO;
import javafx.application.Platform;

import java.util.Map;
import java.util.function.Consumer;

public class UIAdapter {
    private final Consumer<String> introduceFlowName;
    private final Consumer<String> introduceFlowID;
    private final Consumer<String> introduceFlowEndTime;
    private final Consumer<String> introduceFlowDuration;
    private final Consumer<String> introduceFlowResult;
    private final Consumer<Map.Entry<DataInFlowDTO, Object>> introduceNewOutput;
    private final Consumer<String> introduceNewInput;
    private final Consumer<String> introduceNewStep;
    private final Consumer<String> introduceFlowStartTime;
    private final Runnable clearStepsItems;
    private final Runnable clearOutputsItems;
    private final Runnable clearInputsItems;



    public UIAdapter(Consumer<String> introduceFlowName, Consumer<String> introduceFlowID, Consumer<String> introduceFlowEndTime, Consumer<String> introduceFlowDuration, Consumer<String> introduceFlowResult, Consumer<Map.Entry<DataInFlowDTO, Object>> introduceNewOutput, Consumer<String> introduceNewInput, Consumer<String> introduceNewStep, Consumer<String> introduceFlowStartTime, Runnable clearStepsItems, Runnable clearOutputsItems, Runnable clearInputsItems) {
        this.introduceFlowName = introduceFlowName;
        this.introduceFlowID = introduceFlowID;
        this.introduceFlowEndTime = introduceFlowEndTime;
        this.introduceFlowDuration = introduceFlowDuration;
        this.introduceFlowResult = introduceFlowResult;
        this.introduceNewOutput = introduceNewOutput;
        this.introduceNewInput = introduceNewInput;
        this.introduceNewStep = introduceNewStep;
        this.introduceFlowStartTime = introduceFlowStartTime;
        this.clearStepsItems = clearStepsItems;
        this.clearOutputsItems = clearOutputsItems;
        this.clearInputsItems = clearInputsItems;
    }

    public void updateFlowName(String name) {
        Platform.runLater(
                () -> {
                    introduceFlowName.accept(name);
                }
        );
    }

    public void updateFlowID(String id) {
        Platform.runLater(
                () -> {
                    introduceFlowID.accept(id);
                }
        );
    }

    public void updateFlowEndTime(String endTime){
        Platform.runLater(
                () -> {
                    introduceFlowEndTime.accept(endTime);
                }
        );
    }
    public void updateFlowStartTime(String startTime){
        Platform.runLater(
                () -> {
                    introduceFlowStartTime.accept(startTime);
                }
        );
    }

    public void addNewStep(String stepName){
        Platform.runLater(
                () -> {
                    introduceNewStep.accept(stepName);
                }
        );
    }



    public void clearStepsItems(){
        Platform.runLater(
                clearStepsItems::run
        );
    }


    public void clearOutputsItems(){
        Platform.runLater(
                clearOutputsItems::run
        );
    }

    public void clearInputsItems(){
        Platform.runLater(
                clearInputsItems::run
        );
    }



    public void updateFlowResult(String result) {
        Platform.runLater(
                () -> {
                    introduceFlowResult.accept(result);
                }
        );
    }

    public void updateFlowDuration(String duration) {
        Platform.runLater(
                () -> {
                    introduceFlowDuration.accept(duration);
                }
        );
    }

    public void addNewOutput(Map.Entry<DataInFlowDTO, Object> entry) {
        Platform.runLater(
                () -> {
                    introduceNewOutput.accept(entry);
                }
        );
    }

    public void addNewInput(String inputName) {
        Platform.runLater(
                () -> {
                    introduceNewInput.accept(inputName);
                }
        );
    }




}
