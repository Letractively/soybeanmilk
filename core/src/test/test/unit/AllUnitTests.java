package test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.unit.core.TestDefaultGenericConverter;
import test.unit.core.TestHashMapObjectSource;
import test.unit.core.TestInvokeStatementParser;
import test.unit.web.TestDispatchServlet;
import test.unit.web.TestWebGenericConverter;
import test.unit.web.TestWebObjectSource;

@RunWith(Suite.class)
@Suite.SuiteClasses({
TestDefaultGenericConverter.class,
TestHashMapObjectSource.class,
TestInvokeStatementParser.class,
TestDispatchServlet.class,

TestDispatchServlet.class,
TestWebGenericConverter.class,
TestWebObjectSource.class
})
public class AllUnitTests {} 
