import controls.RecordCreator;
import records.Task;
import controls.ControlManager;

public class Main {

    public static void main(String[] args) {
        ControlManager controlManager = new ControlManager();
        System.out.println("Поехали!");
        controlManager.menuPrint();
        controlManager.getControlOptions();


    }
}
