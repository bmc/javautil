package org.clapper.util.text;

import java.util.HashMap;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

public class WindowsCmdVariableSubstituterTest
{
    private Map<String,String> vars = new HashMap<String,String>();
    private VariableDereferencer deref;
    private WindowsCmdVariableSubstituter sub =
        new WindowsCmdVariableSubstituter();

    public WindowsCmdVariableSubstituterTest()
    {
    }

    @Before public void init()
    {
        vars.clear();
        vars.put("a", "1");
        vars.put("b", "2");
        vars.put("foo", "barsky");
        vars.put("bar", "a very long value that goes from here to there");
        vars.put("y", "");
        vars.put("longerVariableName", " ");
        deref = new MapVariableDereferencer(vars);
    }

    @Test public void goodSubstitution()
        throws VariableSubstitutionException
    {
        String s = sub.substitute("ab%a%", deref);
        assertEquals("Substitution failure", "ab1", s);

        s = sub.substitute("%a", deref);
        assertEquals("Substitution failure", "%a", s);

        s = sub.substitute("%a%%b%%c%", deref);
        assertEquals("Substitution failure", "12", s);

        s = sub.substitute("%a%%b%%foo%", deref);
        assertEquals("Substitution failure", "12barsky", s);

        s = sub.substitute("%x%", deref);
        assertEquals("Substitution failure", "", s);
    }

    @Test(expected=UndefinedVariableException.class)
    public void badVariable() throws Throwable
    {
        sub.setAbortOnUndefinedVariable(true);
        sub.substitute("%blah%", deref);
    }
    
    @Test(expected=VariableSyntaxException.class)
    public void badSyntax() throws Throwable
    {
        sub.setAbortOnSyntaxError(true);
        sub.substitute("%foo", deref);
    }
}
