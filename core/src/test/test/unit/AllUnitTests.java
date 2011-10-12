package test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.unit.core.TestClassShortName;
import test.unit.core.TestConfigurationParser;
import test.unit.core.TestDefaultExecutor;
import test.unit.core.TestDefaultGenericConverter;
import test.unit.core.TestGenericType;
import test.unit.core.TestHashMapObjectSource;
import test.unit.core.TestInvokeStatementParser;
import test.unit.core.TestPropertyInfo;
import test.unit.web.TestAbstractTargetHandler;
import test.unit.web.TestDefaultWebExecutor;
import test.unit.web.TestDispatchServlet;
import test.unit.web.TestPathNode;
import test.unit.web.TestVariablePath;
import test.unit.web.TestVariablePathMatcher;
import test.unit.web.TestWebConfigurationParser;
import test.unit.web.TestWebGenericConverter;
import test.unit.web.TestWebObjectSource;

@RunWith(Suite.class)
@Suite.SuiteClasses({
TestClassShortName.class,
TestDefaultExecutor.class,
TestDefaultGenericConverter.class,
TestHashMapObjectSource.class,
TestInvokeStatementParser.class,
TestPropertyInfo.class,
TestConfigurationParser.class,
TestGenericType.class,

TestDispatchServlet.class,
TestPathNode.class,
TestVariablePath.class,
TestVariablePathMatcher.class,
TestWebGenericConverter.class,
TestWebObjectSource.class,
TestDefaultWebExecutor.class,
TestWebConfigurationParser.class,
TestAbstractTargetHandler.class
})
public class AllUnitTests{}
