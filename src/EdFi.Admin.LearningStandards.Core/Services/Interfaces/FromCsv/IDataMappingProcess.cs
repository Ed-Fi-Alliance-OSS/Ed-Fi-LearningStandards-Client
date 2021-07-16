// SPDX-License-Identifier: Apache-2.0
// Licensed to the Ed-Fi Alliance under one or more agreements.
// The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
// See the LICENSE and NOTICES files in the project root for more information.

using System.Collections.Generic;
using EdFi.Admin.LearningStandards.Core.Models.FromCsv;
using Newtonsoft.Json.Linq;

namespace EdFi.Admin.LearningStandards.Core.Services.Interfaces.FromCsv
{
    public interface IDataMappingProcess
    {
        JObject ApplyMap(IEnumerable<LearningStandardMetaData> learningStandardMetaData,
            IEnumerable<DataMapper> dataMappers, Dictionary<string, string> csvRow);

        IEnumerable<DataMapper> GetDataMappings();
    }
}
