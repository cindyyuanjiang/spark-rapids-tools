/*
 * Copyright (c) 2024, NVIDIA CORPORATION.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nvidia.spark.rapids.tool.views

import com.nvidia.spark.rapids.tool.analysis.ProfSparkMetricsAnalyzer
import com.nvidia.spark.rapids.tool.profiling.{IOAnalysisProfileResult, JobStageAggTaskMetricsProfileResult, ShuffleSkewProfileResult, SQLDurationExecutorTimeProfileResult, SQLMaxTaskInputSizes, SQLTaskAggMetricsProfileResult}

import org.apache.spark.sql.rapids.tool.profiling.ApplicationInfo

// The profiling shows a single combined view for both Stage/Job-levels which is different from
// the default view that separates between the two.
case class ProfilerAggregatedView(
    jobStageAggs: Seq[JobStageAggTaskMetricsProfileResult],
    taskShuffleSkew: Seq[ShuffleSkewProfileResult],
    sqlAggs: Seq[SQLTaskAggMetricsProfileResult],
    ioAggs: Seq[IOAnalysisProfileResult],
    sqlDurAggs: Seq[SQLDurationExecutorTimeProfileResult],
    maxTaskInputSizes: Seq[SQLMaxTaskInputSizes])

object RawMetricProfilerView  {
  def getAggMetrics(apps: Seq[ApplicationInfo]): ProfilerAggregatedView = {
    val aggMetricsResults = ProfSparkMetricsAnalyzer.getAggregateRawMetrics(apps)
    ProfilerAggregatedView(
      AggMetricsResultSorter.sortJobSparkMetrics(
        aggMetricsResults.stageAggs ++ aggMetricsResults.jobAggs),
      AggMetricsResultSorter.sortShuffleSkew(aggMetricsResults.taskShuffleSkew),
      AggMetricsResultSorter.sortSqlAgg(aggMetricsResults.sqlAggs),
      AggMetricsResultSorter.sortIO(aggMetricsResults.ioAggs),
      AggMetricsResultSorter.sortSqlDurationAgg(aggMetricsResults.sqlDurAggs),
      aggMetricsResults.maxTaskInputSizes)
  }
}