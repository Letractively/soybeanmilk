/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.unit.core.TestClassShortName;
import test.unit.core.TestConfigurationParser;
import test.unit.core.TestDefaultExecutor;
import test.unit.core.TestDefaultGenericConverter;
import test.unit.core.TestHashMapObjectSource;
import test.unit.core.TestInvoke;
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
import test.unit.web.TestDefaultWebObjectSource;

/**
 * @author earthangry@gmail.com
 * @date 2012-5-10
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
TestClassShortName.class,
TestDefaultExecutor.class,
TestInvoke.class,
TestDefaultGenericConverter.class,
TestHashMapObjectSource.class,
TestInvokeStatementParser.class,
TestPropertyInfo.class,
TestConfigurationParser.class,

TestDispatchServlet.class,
TestPathNode.class,
TestVariablePath.class,
TestVariablePathMatcher.class,
TestWebGenericConverter.class,
TestDefaultWebObjectSource.class,
TestDefaultWebExecutor.class,
TestWebConfigurationParser.class,
TestAbstractTargetHandler.class
})
public class AllUnitTests{}
