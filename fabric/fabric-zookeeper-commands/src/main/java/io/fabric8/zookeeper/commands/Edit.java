/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.zookeeper.commands;

import io.fabric8.api.FabricService;
import io.fabric8.api.scr.ValidatingReference;
import io.fabric8.boot.commands.support.AbstractCommandComponent;
import io.fabric8.commands.support.ZNodeCompleter;
import io.fabric8.zookeeper.curator.CuratorFrameworkLocator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.Function;
import org.jledit.EditorFactory;

@Component(immediate = true)
@Service({Function.class, AbstractCommand.class})
@org.apache.felix.scr.annotations.Properties({
        @Property(name = "osgi.command.scope", value = Edit.SCOPE_VALUE),
        @Property(name = "osgi.command.function", value = Edit.FUNCTION_VALUE)
})
public class Edit extends AbstractCommandComponent {

    public static final String SCOPE_VALUE = "zk";
    public static final String FUNCTION_VALUE = "edit";
    public static final String DESCRIPTION = "Edits a znode's data";

    @Reference(referenceInterface = EditorFactory.class)
    private final ValidatingReference<EditorFactory> editorFactory = new ValidatingReference<EditorFactory>();

    // Completers
    @Reference(referenceInterface = ZNodeCompleter.class, bind = "bindZnodeCompleter", unbind = "unbindZnodeCompleter")
    private ZNodeCompleter zNodeCompleter; // dummy field

    @Activate
    void activate() {
        activateComponent();
    }

    @Deactivate
    void deactivate() {
        deactivateComponent();
    }

    @Override
    public Action createNewAction() {
        assertValid();
        // this is how we get hold of the curator framework
        CuratorFramework curator = CuratorFrameworkLocator.getCuratorFramework();
        return new EditAction(curator, editorFactory.get());
    }

    void bindEditorFactory(EditorFactory editorFactory) {
        this.editorFactory.bind(editorFactory);
    }

    void unbindEditorFactory(EditorFactory editorFactory) {
        this.editorFactory.unbind(editorFactory);
    }

    void bindZnodeCompleter(ZNodeCompleter completer) {
        bindCompleter(completer);
    }

    void unbindZnodeCompleter(ZNodeCompleter completer) {
        unbindCompleter(completer);
    }


}
