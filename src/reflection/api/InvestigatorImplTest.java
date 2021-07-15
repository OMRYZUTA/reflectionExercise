package reflection.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InvestigatorImplTest  {
    InvestigatorImpl ObjectInvestigator =new InvestigatorImpl();
    InvestigatorImpl StringInvestigator =new InvestigatorImpl();
    InvestigatorImpl HashSetInvestigator =new InvestigatorImpl();
    InvestigatorImpl ArrayListInvestigator =new InvestigatorImpl();
    @BeforeEach
    void setUp() {
        ObjectInvestigator.load(new Object());
        StringInvestigator.load(new String());
        HashSetInvestigator.load(new HashSet<String>());
        ArrayListInvestigator.load(new ArrayList<String>());
    }

    @org.junit.jupiter.api.Test
    void load() {
        String name = "hi there";
        Class stringClass = name.getClass();
        InvestigatorImpl investigator = new InvestigatorImpl();
        investigator.load(name);
        assertTrue(investigator.isLoaded());
    }

    @Test
    void isLoaded() {
        InvestigatorImpl investigator = new InvestigatorImpl();
        assertFalse(investigator.isLoaded());
    }

    @Test
    void getTotalNumberOfMethods() {
        assertEquals(12, ObjectInvestigator.getTotalNumberOfMethods());
    }

    @Test
    void getTotalNumberOfConstructors() {
        assertEquals(1, ObjectInvestigator.getTotalNumberOfConstructors());
    }

    @Test
    void getTotalNumberOfFields() {
        assertEquals(0, ObjectInvestigator.getTotalNumberOfFields());
    }

    @Test
    void getAllImplementedInterfaces() {
        Set<String> stringInterfaces = StringInvestigator.getAllImplementedInterfaces();
        Set<String> expected = new HashSet<>();
        expected.add("Serializable");
        expected.add("Comparable");
        expected.add("CharSequence");
        assertEquals(stringInterfaces, expected);
    }

    @Test
    void getCountOfConstantFields() {
        int finalFields = StringInvestigator.getCountOfConstantFields();
        assertEquals(4,finalFields);
    }

    @Test
    void getCountOfStaticMethods() {
        int countOfStaticMethods = StringInvestigator.getCountOfStaticMethods();
        assertEquals(20, countOfStaticMethods);
    }

    @Test
    void isExtending() {
        assertFalse(StringInvestigator.isExtending());
    }

    @Test
    void getParentClassSimpleName() {
        String parentSimpleName = StringInvestigator.getParentClassSimpleName();
        assertEquals("Object",parentSimpleName);
    }

    @Test
    void isParentClassAbstract() {
        assertTrue(HashSetInvestigator.isParentClassAbstract());
    }
    @Test
    void isParentClassAbstractForObject() {
        assertFalse(ObjectInvestigator.isParentClassAbstract());
    }

    @Test
    void isParentClassAbstractForString() {
        assertFalse(StringInvestigator.isParentClassAbstract());
    }

    @Test
    void getNamesOfAllFieldsIncludingInheritanceChainHashSet() {
        Set<String> HashSetFields = HashSetInvestigator.getNamesOfAllFieldsIncludingInheritanceChain();
        Set<String> expected = new HashSet<>();
        expected.add("PRESENT");
        expected.add("serialVersionUID");
        expected.add("map");
        expected.add("MAX_ARRAY_SIZE");
        assertEquals(HashSetFields, expected);

    }

    @Test
    void invokeMethodThatReturnsInt() {
        int returnedInt = StringInvestigator.invokeMethodThatReturnsInt("hashCode");
        int trial = new String().hashCode();
        assertEquals(new String().hashCode(),returnedInt);
    }

    @Test
    void createInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object expectedInstance = new HashSet<String>();
        Object actualInstance = HashSetInvestigator.createInstance(0);
        assertEquals(expectedInstance,actualInstance);
    }

    @Test
    void elevateMethodAndInvoke() throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parametersTypes = {Integer.TYPE};
        int result = ( int) ArrayListInvestigator.elevateMethodAndInvoke("hugeCapacity",parametersTypes, Integer.MAX_VALUE-7 );
        assertEquals(Integer.MAX_VALUE,result);
    }

    @Test
    void getInheritanceChain() {
        String expectedChain = "Object->AbstractCollection->AbstractList->ArrayList";
        String actualChain = ArrayListInvestigator.getInheritanceChain("->");
        assertEquals(expectedChain, actualChain);
    }
}