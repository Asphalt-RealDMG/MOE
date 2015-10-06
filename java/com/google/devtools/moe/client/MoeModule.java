// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.devtools.moe.client;

import com.google.devtools.moe.client.database.FileDb;
import com.google.devtools.moe.client.database.RepositoryEquivalence;
import com.google.devtools.moe.client.directives.DirectivesModule;
import com.google.devtools.moe.client.options.OptionsModule;
import com.google.devtools.moe.client.project.FileReadingProjectContextFactory;
import com.google.devtools.moe.client.project.ProjectContextFactory;
import com.google.devtools.moe.client.repositories.Repositories;
import com.google.devtools.moe.client.tools.FileDifference.ConcreteFileDiffer;
import com.google.devtools.moe.client.tools.FileDifference.FileDiffer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import dagger.Module;
import dagger.Provides;

import java.lang.reflect.Type;

import javax.inject.Singleton;

/**
 * Module to register bindings for MOE.
 *
 * @author cgruber@google.com (Christian Gruber)
 */
@Module(
  includes = {
    Repositories.Defaults.class,
    OptionsModule.class,
    DirectivesModule.class,
    FileDb.Module.class
  }
)
public class MoeModule {
  @Provides
  @Singleton
  Ui ui(SystemUi sysui) {
    return sysui;
  }

  /* Alias to UI which extends this interface */
  @Provides
  public Messenger messenger(Ui ui) {
    return ui;
  }

  @Provides
  @Singleton
  ProjectContextFactory projectContextFactory(FileReadingProjectContextFactory factory) {
    return factory;
  }

  @Provides
  @Singleton
  CommandRunner commandRunner(SystemCommandRunner runner) {
    return runner;
  }

  @Provides
  @Singleton
  FileSystem fileSystem(SystemFileSystem sysfs) {
    return sysfs;
  }

  @Provides
  @Singleton
  FileDiffer fileDiffer(ConcreteFileDiffer cfd) {
    return cfd;
  }

  @Provides
  @Singleton
  public static Gson provideGson() {
    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(
            RepositoryEquivalence.class, new RepositoryEquivalence.Serializer())
        .registerTypeAdapter(JsonObject.class, new JsonObjectDeserializer())
        .create();
  }

  /**
   * Helper class to deserialize raw Json in a config.
   */
  static class JsonObjectDeserializer implements JsonDeserializer<JsonObject> {
    @Override
    public JsonObject deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return json.getAsJsonObject();
    }
  }
}
