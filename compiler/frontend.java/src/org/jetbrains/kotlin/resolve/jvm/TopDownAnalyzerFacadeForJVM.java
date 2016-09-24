/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package org.jetbrains.kotlin.resolve.jvm;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analyzer.AnalysisResult;
import org.jetbrains.kotlin.config.CommonConfigurationKeys;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.config.JVMConfigurationKeys;
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl;
import org.jetbrains.kotlin.context.ContextKt;
import org.jetbrains.kotlin.context.ModuleContext;
import org.jetbrains.kotlin.context.MutableModuleContext;
import org.jetbrains.kotlin.context.ProjectContext;
import org.jetbrains.kotlin.descriptors.ModuleDescriptor;
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider;
import org.jetbrains.kotlin.descriptors.PackagePartProvider;
import org.jetbrains.kotlin.frontend.java.di.ContainerForTopDownAnalyzerForJvm;
import org.jetbrains.kotlin.frontend.java.di.InjectionKt;
import org.jetbrains.kotlin.incremental.components.LookupTracker;
import org.jetbrains.kotlin.load.kotlin.incremental.IncrementalPackageFragmentProvider;
import org.jetbrains.kotlin.load.kotlin.incremental.IncrementalPackagePartProvider;
import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCompilationComponents;
import org.jetbrains.kotlin.modules.Module;
import org.jetbrains.kotlin.modules.TargetId;
import org.jetbrains.kotlin.modules.TargetIdKt;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.platform.JvmBuiltIns;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.resolve.BindingTrace;
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode;
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisCompletedHandlerExtension;
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension;
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform;
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory;
import org.jetbrains.kotlin.storage.StorageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum TopDownAnalyzerFacadeForJVM {

    INSTANCE;

    @NotNull
    public static AnalysisResult analyzeFilesWithJavaIntegration(
            @NotNull ModuleContext moduleContext,
            @NotNull Collection<KtFile> files,
            @NotNull BindingTrace trace,
            @NotNull CompilerConfiguration configuration,
            @NotNull PackagePartProvider initialPackagePartProvider
    ) {
        StorageManager storageManager = moduleContext.getStorageManager();
        Project project = moduleContext.getProject();
        ModuleDescriptor moduleDescriptor = moduleContext.getModule();

        IncrementalCompilationComponents incrementalComponents = configuration.get(JVMConfigurationKeys.INCREMENTAL_COMPILATION_COMPONENTS);
        LookupTracker lookupTracker =
                incrementalComponents != null ? incrementalComponents.getLookupTracker() : LookupTracker.Companion.getDO_NOTHING();

        List<TargetId> targetIds = null;
        List<Module> modules = configuration.get(JVMConfigurationKeys.MODULES);
        if (modules != null) {
            targetIds = new ArrayList<TargetId>(modules.size());

            for (Module module : modules) {
                targetIds.add(TargetIdKt.TargetId(module));
            }
        }

        PackagePartProvider packagePartProvider =
                IncrementalPackagePartProvider.create(initialPackagePartProvider, files, targetIds, incrementalComponents, storageManager);

        ContainerForTopDownAnalyzerForJvm container = InjectionKt.createContainerForTopDownAnalyzerForJvm(
                moduleContext,
                trace,
                new FileBasedDeclarationProviderFactory(storageManager, files),
                GlobalSearchScope.allScope(project),
                lookupTracker,
                packagePartProvider,
                configuration.get(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS, LanguageVersionSettingsImpl.DEFAULT)
        );

        List<PackageFragmentProvider> additionalProviders = new ArrayList<PackageFragmentProvider>();

        if (targetIds != null && incrementalComponents != null) {
            for (TargetId targetId : targetIds) {
                additionalProviders.add(
                        new IncrementalPackageFragmentProvider(
                                files, moduleDescriptor, storageManager, container.getDeserializationComponentsForJava().getComponents(),
                                incrementalComponents.getIncrementalCache(targetId), targetId
                        )
                );
            }
        }
        additionalProviders.add(container.getJavaDescriptorResolver().getPackageFragmentProvider());

        for (PackageFragmentProviderExtension extension : PackageFragmentProviderExtension.Companion.getInstances(project)) {
            PackageFragmentProvider provider = extension.getPackageFragmentProvider(project, moduleDescriptor, storageManager, trace, null);
            if (provider != null) additionalProviders.add(provider);
        }

        container.getLazyTopDownAnalyzerForTopLevel().analyzeFiles(TopDownAnalysisMode.TopLevelDeclarations, files, additionalProviders);

        for (AnalysisCompletedHandlerExtension extension : AnalysisCompletedHandlerExtension.Companion.getInstances(project)) {
            AnalysisResult result = extension.analysisCompleted(project, moduleDescriptor, trace, files);
            if (result != null) return result;
        }

        return AnalysisResult.success(trace.getBindingContext(), moduleDescriptor);
    }

    @NotNull
    public static MutableModuleContext createContextWithSealedModule(
            @NotNull Project project, @NotNull CompilerConfiguration configuration
    ) {
        ProjectContext projectContext = ContextKt.ProjectContext(project);
        JvmBuiltIns builtIns = new JvmBuiltIns(projectContext.getStorageManager());
        MutableModuleContext context = ContextKt.ContextForNewModule(
                projectContext, Name.special("<" + configuration.getNotNull(CommonConfigurationKeys.MODULE_NAME) + ">"),
                JvmPlatform.INSTANCE, builtIns
        );
        context.setDependencies(context.getModule(), builtIns.getBuiltInsModule());
        return context;
    }
}
