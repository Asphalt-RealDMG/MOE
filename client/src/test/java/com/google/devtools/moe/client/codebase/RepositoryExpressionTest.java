/*
 * Copyright (c) 2011 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.moe.client.codebase;

import com.google.common.collect.ImmutableMap;
import com.google.devtools.moe.client.Injector;
import com.google.devtools.moe.client.MoeProblem;
import com.google.devtools.moe.client.NullFileSystemModule;
import com.google.devtools.moe.client.SystemCommandRunner;
import com.google.devtools.moe.client.codebase.RepositoryExpression.WriterFactory;
import com.google.devtools.moe.client.project.ProjectContext;
import com.google.devtools.moe.client.project.ProjectContext.NoopProjectContext;
import com.google.devtools.moe.client.repositories.RepositoryType;
import com.google.devtools.moe.client.testing.DummyRepositoryFactory;
import com.google.devtools.moe.client.testing.TestingModule;
import javax.inject.Singleton;
import junit.framework.TestCase;

public class RepositoryExpressionTest extends TestCase {

  // TODO(cgruber): Rework these when statics aren't inherent in the design.
  @dagger.Component(
      modules = {TestingModule.class, SystemCommandRunner.Module.class, NullFileSystemModule.class})
  @Singleton
  interface Component {
    Injector context(); // TODO (b/19676630) Remove when bug is fixed.
  }

  private WriterFactory writerFactory;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Injector.INSTANCE = DaggerExpressionProcessingTest_Component.create().context();
    writerFactory = new WriterFactory(Injector.INSTANCE.ui());
  }

  public void testMakeWriter_NonexistentRepository() throws Exception {
    try {
      RepositoryExpression expression = new RepositoryExpression("internal");
      writerFactory.createWriter(expression, new NoopProjectContext());
      fail();
    } catch (MoeProblem expected) {
      assertEquals("No such repository 'internal' in the config. Found: []", expected.getMessage());
    }
  }

  public void testMakeWriter_DummyRepository() throws Exception {
    final RepositoryType.Factory repositoryFactory = new DummyRepositoryFactory();
    ProjectContext context =
        new NoopProjectContext() {
          @Override
          public ImmutableMap<String, RepositoryType> repositories() {
            return ImmutableMap.of("internal", repositoryFactory.create("internal", null));
          }
        };
    RepositoryExpression expression = new RepositoryExpression("internal");
    writerFactory.createWriter(expression, context);
  }
}
