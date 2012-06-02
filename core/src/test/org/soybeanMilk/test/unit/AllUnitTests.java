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

package org.soybeanMilk.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.soybeanMilk.test.unit.core.TestClassShortName;
import org.soybeanMilk.test.unit.core.TestConfigurationParser;
import org.soybeanMilk.test.unit.core.TestDefaultExecutor;
import org.soybeanMilk.test.unit.core.TestDefaultGenericConverter;
import org.soybeanMilk.test.unit.core.TestHashMapObjectSource;
import org.soybeanMilk.test.unit.core.TestInvoke;
import org.soybeanMilk.test.unit.core.TestInvokeStatementParser;
import org.soybeanMilk.test.unit.core.TestPropertyInfo;
import org.soybeanMilk.test.unit.web.TestAbstractTargetHandler;
import org.soybeanMilk.test.unit.web.TestDefaultWebExecutor;
import org.soybeanMilk.test.unit.web.TestDefaultWebObjectSource;
import org.soybeanMilk.test.unit.web.TestDispatchServlet;
import org.soybeanMilk.test.unit.web.TestPathNode;
import org.soybeanMilk.test.unit.web.TestVariablePath;
import org.soybeanMilk.test.unit.web.TestVariablePathMatcher;
import org.soybeanMilk.test.unit.web.TestWebConfigurationParser;
import org.soybeanMilk.test.unit.web.TestWebGenericConverter;


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
