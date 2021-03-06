/*
 * Copyright © 2018 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.hydrator.plugin.batch.sink;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.etl.api.batch.BatchSink;
import co.cask.cdap.etl.api.batch.BatchSinkContext;
import co.cask.hydrator.format.plugin.AbstractFileSink;
import co.cask.hydrator.format.plugin.AbstractFileSinkConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Writes to the FileSystem.
 */
@Plugin(type = BatchSink.PLUGIN_TYPE)
@Name("File")
@Description("Writes to the FileSystem.")
public class FileSink extends AbstractFileSink<FileSink.Conf> {
  private final Conf config;

  public FileSink(Conf config) {
    super(config);
    this.config = config;
  }

  @Override
  protected Map<String, String> getFileSystemProperties(BatchSinkContext context) {
    return config.getFSProperties();
  }

  /**
   * Config for File Sink.
   */
  public static class Conf extends AbstractFileSinkConfig {
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() { }.getType();

    @Macro
    @Description("Destination path prefix. For example, 'hdfs://mycluster.net:8020/output'")
    private String path;

    @Macro
    @Nullable
    @Description("Advanced feature to specify any additional properties that should be used with the sink.")
    private String fileSystemProperties;

    private Conf() {
      fileSystemProperties = "{}";
    }

    @Override
    public String getPath() {
      return path;
    }

    private Map<String, String> getFSProperties() {
      if (fileSystemProperties == null || fileSystemProperties.isEmpty()) {
        return Collections.emptyMap();
      }
      try {
        return GSON.fromJson(fileSystemProperties, MAP_TYPE);
      } catch (JsonSyntaxException e) {
        throw new IllegalArgumentException("Unable to parse filesystem properties: " + e.getMessage(), e);
      }
    }
  }
}
