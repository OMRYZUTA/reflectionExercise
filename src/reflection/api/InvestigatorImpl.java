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
        for(Class<?> classInfo : interfaces){
            names.add((classInfo.getSimpleName()));
        }

        return names;
    }

    @Override
    public int getCountOfConstantFields() {
        int finalsCount =0;
        Field[] fields = m_ClassOfInstance.getDeclaredFields();
        for(Field field :fields) {
            if(Modifier.isFinal(field.getModifiers())){
                finalsCount++;
            }
        }

        return finalsCount;
    }

    @Override
    public int getCountOfStaticMethods() {
        Method[] methods = m_ClassOfInstance.getDeclaredMethods();
        int countOfStaticMethods = 0;
        for (Method method: methods){
            if (Modifier.isStatic(method.getModifiers())){
                countOfStaticMethods++;
            }
        }
        return countOfStaticMethods;
    }

    @Override
    public boolean isExtending() {
        boolean answer = false;
        Class<?> classOfSuper = m_ClassOfInstance.getSuperclass();
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
        boolean result = false;
        if(superClass != null){
            result =Modifier.isAbstract(superClass.getModifiers());
        }
        return result;
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Class<?> currentClass = m_ClassOfInstance;
        Set<String> AllFields = new HashSet<>();
        while(currentClass != null){
            Field[] fields = currentClass.getDeclaredFields();
            for(Field field : fields){
                AllFields.add(field.getName());
            }

            currentClass = currentClass.getSuperclass();
        }
        return AllFields;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        int result =-1;
        try {
            Method method = m_ClassOfInstance.getMethod(methodName);
            result = (int) method.invoke(m_Instance, args);
            return  result;
        }
        catch(Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {
        Object instance = null;
        Constructor<?>[] constructors = m_ClassOfInstance.getConstructors();
        Constructor<?> chosenConstructor = null;
        for(Constructor<?> constructor : constructors){
            if(constructor.getParameterCount()==numberOfArgs){
                chosenConstructor =constructor;
                break;
            }
        }
        if (chosenConstructor != null){
            try {
                instance = chosenConstructor.newInstance(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        Method method = null;
        try {
            method = m_ClassOfInstance.getDeclaredMethod(name,parametersTypes);
            assert method != null;
            method.setAccessible(true);
            return method.invoke(m_Instance ,args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        Class<?> currentClass = m_ClassOfInstance;
        StringBuilder chain = new StringBuilder();
        while(currentClass !=null){
            chain.insert(0, currentClass.getSimpleName());
            currentClass= currentClass.getSuperclass();
            if(currentClass != null)
            {
                chain.insert(0, delimiter);
            }
        }
        return chain.toString();
    }
}
