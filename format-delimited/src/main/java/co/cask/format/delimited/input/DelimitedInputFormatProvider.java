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

package co.cask.format.delimited.input;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.plugin.PluginClass;
import co.cask.cdap.api.plugin.PluginPropertyField;
import co.cask.hydrator.format.input.PathTrackingConfig;
import co.cask.hydrator.format.input.PathTrackingInputFormatProvider;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Reads delimited text into StructuredRecords.
 */
@Plugin(type = "inputformat")
@Name(DelimitedInputFormatProvider.NAME)
@Description(DelimitedInputFormatProvider.DESC)
public class DelimitedInputFormatProvider extends PathTrackingInputFormatProvider<DelimitedInputFormatProvider.Conf> {
  public static final PluginClass PLUGIN_CLASS = getPluginClass();
  static final String NAME = "delimited";
  static final String DESC = "Plugin for reading files in delimited format.";
  private final Conf conf;

  public DelimitedInputFormatProvider(Conf conf) {
    super(conf);
    this.conf = conf;
  }

  @Override
  public String getInputFormatClassName() {
    return CombineDelimitedInputFormat.class.getName();
  }

  @Override
  protected void validate() {
    if (conf.getSchema() == null) {
      throw new IllegalArgumentException("Delimited format cannot be used without specifying a schema.");
    }
  }

  @Override
  protected void addFormatProperties(Map<String, String> properties) {
    properties.put(PathTrackingDelimitedInputFormat.DELIMITER, conf.delimiter == null ? "," : conf.delimiter);
  }

  /**
   * Plugin config for delimited input format
   */
  public static class Conf extends PathTrackingConfig {
    private static final String DELIMITER_DESC = "Delimiter to use to separate record fields.";

    @Macro
    @Nullable
    @Description(DELIMITER_DESC)
    private String delimiter;
  }


  private static PluginClass getPluginClass() {
    Map<String, PluginPropertyField> properties = new HashMap<>(PathTrackingConfig.FIELDS);
    properties.put("delimiter", new PluginPropertyField("delimiter", Conf.DELIMITER_DESC, "string", false, true));
    return new PluginClass("inputformat", NAME, DESC, DelimitedInputFormatProvider.class.getName(),
                           "conf", properties);
  }
}
