package reflection.api;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class InvestigatorImpl implements Investigator {
    private Class<?> m_ClassOfInstance;
    private Object m_Instance;

    public boolean isLoaded(){
        return (m_ClassOfInstance !=null);
    }
    @Override
    public void load(Object anInstanceOfSomething) {
        m_Instance = anInstanceOfSomething;
        m_ClassOfInstance = anInstanceOfSomething.getClass();
    }

    @Override
    public int getTotalNumberOfMethods() {
        return m_ClassOfInstance.getDeclaredMethods().length;
    }

    @Override
    public int getTotalNumberOfConstructors() {
        return m_ClassOfInstance.getDeclaredConstructors().length;
    }

    @Override
    public int getTotalNumberOfFields() {
        return m_ClassOfInstance.getDeclaredFields().length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Class<?>[] interfaces = m_ClassOfInstance.getInterfaces();
        Set<String> names = new HashSet<>();
        buildNameSet(interfaces, names);
        return names;
    }

    private void buildNameSet(Class<?>[] interfaces, Set<String> names) {
        for(Class<?> classInfo : interfaces)
            names.add((classInfo.getSimpleName()));
    }

    @Override
    public int getCountOfConstantFields() {
        Field[] fields = m_ClassOfInstance.getDeclaredFields();
        return increaseCount(fields);
    }

    private int increaseCount(Field[] fields) {
        int finalsCount =0;
        for(Field field : fields)
            finalsCount += increaseIfStatic( Modifier.isFinal(field.getModifiers()));
        return finalsCount;
    }

    @Override
    public int getCountOfStaticMethods() {
        Method[] methods = m_ClassOfInstance.getDeclaredMethods();
        return increaseCount(methods);
    }

    private int increaseCount(Method[] methods) {
        int countOfStaticMethods = 0;
        for (Method method: methods)
            countOfStaticMethods += increaseIfStatic(Modifier.isStatic(method.getModifiers()));
        return countOfStaticMethods;
    }

    private int increaseIfStatic( boolean aStatic) {
        return aStatic? 1:0;
    }

    @Override
    public boolean isExtending() {
        Class<?> classOfSuper = m_ClassOfInstance.getSuperclass();
        return checkIfExtending(classOfSuper);
    }

    private boolean checkIfExtending(Class<?> classOfSuper) {
        boolean answer = false;

        if(classOfSuper != null && Object.class != classOfSuper){
            answer = true;
        }
        return answer;
    }

    @Override
    public String getParentClassSimpleName() {
        return  m_ClassOfInstance.getSuperclass().getSimpleName();
    }

    @Override
    public boolean isParentClassAbstract() {
        Class<?> superClass = m_ClassOfInstance.getSuperclass();
        return CheckIfParentIsAbstract(superClass);
    }

    private boolean CheckIfParentIsAbstract(Class<?> superClass) {
        boolean result = false;
        if(superClass != null){
            result =Modifier.isAbstract(superClass.getModifiers());
        }
        return result;
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Class<?> currentClass = m_ClassOfInstance;
        return getNamesOfAllFieldsInChain(currentClass);
    }

    private Set<String> getNamesOfAllFieldsInChain(Class<?> currentClass) {
        Set<String> AllFields = new HashSet<>();
        while(currentClass != null){
            currentClass = tryAddCurrentClassFields(currentClass, AllFields);
        }
        return AllFields;
    }

    private Class<?> tryAddCurrentClassFields(Class<?> currentClass, Set<String> AllFields) {
        Field[] fields = currentClass.getDeclaredFields();
        for(Field field : fields){
            AllFields.add(field.getName());
        }

        currentClass = currentClass.getSuperclass();
        return currentClass;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        int result =-1;
        try {
            Method method = m_ClassOfInstance.getMethod(methodName);
            result = (int) method.invoke(m_Instance, args);
        }
        catch(Exception e) {
        }
        return result;
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {
        Object instance = null;
        Constructor<?>[] constructors = m_ClassOfInstance.getConstructors();
        Constructor<?> chosenConstructor = chooseConstructor(numberOfArgs, constructors);
        instance = getObject(instance, chosenConstructor, args);
        return instance;
    }

    private Object getObject(Object instance, Constructor<?> chosenConstructor, Object[] args) {
        if (chosenConstructor != null){
            try {
                instance = chosenConstructor.newInstance(args);
            }
            catch (Exception e) {
            }
        }
        return instance;
    }

    private Constructor<?> chooseConstructor(int numberOfArgs, Constructor<?>[] constructors) {
        Constructor<?> chosenConstructor = null;
        for(Constructor<?> constructor : constructors){
            if(constructor.getParameterCount()== numberOfArgs){
                chosenConstructor =constructor;
                break;
            }
        }
        return chosenConstructor;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        Method method = null;
        Object result = null;
        try {
            method = m_ClassOfInstance.getDeclaredMethod(name,parametersTypes);
            if (method == null) throw new AssertionError();
            method.setAccessible(true);
            result = method.invoke(m_Instance ,args);
        }
        catch (Throwable e) {
        }

        return result;
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        Class<?> currentClass = m_ClassOfInstance;
        StringBuilder chain = new StringBuilder();
        buildStringChain(delimiter, currentClass, chain);
        return chain.toString();
    }

    private void buildStringChain(String delimiter, Class<?> currentClass, StringBuilder chain) {
        while(currentClass !=null){
            chain.insert(0, currentClass.getSimpleName());
            currentClass = currentClass.getSuperclass();
            if(currentClass != null)
            {
                chain.insert(0, delimiter);
            }
        }
    }
}
