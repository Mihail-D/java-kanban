package controls;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    TaskManager manager = Managers.getDefault();

    protected InMemoryTaskManagerTest() {
        super();
    }

    @Override
    InMemoryTaskManager createManager() {
        return (InMemoryTaskManager) manager;
    }
}