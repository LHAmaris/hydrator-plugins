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

package co.cask.hydrator.format.input;

import co.cask.cdap.api.data.format.StructuredRecord;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

import java.io.IOException;

/**
 * Similar to CombineTextInputFormat except it uses PathTrackingInputFormat to keep track of filepaths that
 * records were read from.
 */
public abstract class CombinePathTrackingInputFormat extends CombineFileInputFormat<NullWritable, StructuredRecord> {

  /**
   * Creates a RecordReader that delegates to some other RecordReader for each path in the input split.
   * The header for each file is set in the context Configuration to make it available to the delegate RecordReaders.
   */
  @Override
  public RecordReader<NullWritable, StructuredRecord> createRecordReader(InputSplit split, TaskAttemptContext context)
    throws IOException {
    return new CombineFileRecordReader<>((CombineFileSplit) split, context, getRecordReaderClass());
  }

  /**
   * Get the wrapper record reader class that's responsible for delegating to a corresponding RecordReader in
   * {@link PathTrackingInputFormat}. All it does is pick the i'th path in the CombineFileSplit to create a
   * FileSplit and use the delegate RecordReader to read that split.
   */
  protected abstract Class<? extends RecordReader<NullWritable, StructuredRecord>> getRecordReaderClass();

}
