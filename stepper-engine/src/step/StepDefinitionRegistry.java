package step;

import flow.execution.context.StepExecutionContext;
import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;
import step.api.StepResult;
import step.impl.*;

import java.util.List;

public enum StepDefinitionRegistry implements StepDefinition
{
    FILE_DUMPER(new FileDumperStep()),
    SPEND_SOME_TIME(new SpendSomeTimeStep()),

    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolder()),
    FILES_DELETER(new FilesDeleterStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractorStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    PROPERTIES_EXPORTER(new PropertiesExporterStep()),
    ZIPPER(new ZipperStep()),
    COMMAND_LINE(new CommandLineStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }


    @Override
    public String getName() {
        return this.stepDefinition.getName();
    }

    @Override
    public void setName(String name) {
        this.stepDefinition.setName(name);
    }

    @Override
    public boolean isReadonly() {
        return  stepDefinition.isReadonly();
    }

    @Override
    public List<DataDefinitionDeclaration> inputs() {
        return stepDefinition.inputs();
    }

    @Override
    public List<DataDefinitionDeclaration> outputs() {
        return stepDefinition.outputs();
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        return stepDefinition.invoke(context);
    }
}
