/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.intelligence.instrumentation;

import android.content.Context;
import android.text.TextUtils;

//import com.android.settings.intelligence.nano.SettingsIntelligenceLogProto
//        .SettingsIntelligenceEvent;
import com.android.settings.intelligence.SettingsIntelligenceLogProto;
import com.android.settings.intelligence.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class MetricsFeatureProvider {

    protected Context mContext;
    protected List<EventLogger> mLoggers;

    public MetricsFeatureProvider(Context context) {
        mContext = context;
        mLoggers = new ArrayList<>();
        mLoggers.add(new LocalEventLogger());
    }

    public void logGetSuggestion(List<String> ids, long latency) {
//        final SettingsIntelligenceLogProto.SettingsIntelligenceEvent event = new SettingsIntelligenceLogProto.SettingsIntelligenceEvent();
//        event.eventType = SettingsIntelligenceLogProto.SettingsIntelligenceEvent.EventType.GET_SUGGESTION;
//        event.latencyMillis = latency;
//        if (ids != null) {
//            event.suggestionIds = ids.toArray(new String[0]);
//        }
//        logEvent(event);
    }

    public void logDismissSuggestion(String id, long latency) {
//        final SettingsIntelligenceLogProto.SettingsIntelligenceEvent event = new SettingsIntelligenceLogProto.SettingsIntelligenceEvent();
//        event.eventType = SettingsIntelligenceLogProto.SettingsIntelligenceEvent.EventType.DISMISS_SUGGESTION;
//        event.latencyMillis = latency;
//        if (!TextUtils.isEmpty(id)) {
//            event.suggestionIds = new String[]{id};
//        }
//        logEvent(event);
    }

    public void logLaunchSuggestion(String id, long latency) {
//        final SettingsIntelligenceLogProto.SettingsIntelligenceEvent event = new SettingsIntelligenceLogProto.SettingsIntelligenceEvent();
//        event.eventType = SettingsIntelligenceLogProto.SettingsIntelligenceEvent.EventType.LAUNCH_SUGGESTION;
//        event.latencyMillis = latency;
//        if (!TextUtils.isEmpty(id)) {
//            event.suggestionIds = new String[]{id};
//        }
//        logEvent(event);
    }

    public void logSearchResultClick(SearchResult result, String query, int type, int count,
            int rank) {
//        final SettingsIntelligenceLogProto.SettingsIntelligenceEvent event = new SettingsIntelligenceLogProto.SettingsIntelligenceEvent();
//        event.eventType = SettingsIntelligenceLogProto.SettingsIntelligenceEvent.EventType.valueOf(type);
//        event.searchResultMetadata = new SettingsIntelligenceLogProto.SettingsIntelligenceEvent.SearchResultMetadata();
//        event.searchResultMetadata.resultCount = count;
//        event.searchResultMetadata.searchResultRank = rank;
//        event.searchResultMetadata.searchResultKey = result.dataKey != null ? result.dataKey : "";
//        event.searchResultMetadata.searchQueryLength = query != null ? query.length() : 0;
//        logEvent(event);
    }

    public void logEvent(int eventType) {
        logEvent(eventType, 0 /* latency */);
    }

    public void logEvent(int eventType, long latency) {
//        final SettingsIntelligenceLogProto.SettingsIntelligenceEvent event = new SettingsIntelligenceLogProto.SettingsIntelligenceEvent();
//        event.eventType = SettingsIntelligenceLogProto.SettingsIntelligenceEvent.EventType.valueOf(eventType);
//        event.latencyMillis = latency;
//        logEvent(event);
    }

    private void logEvent(SettingsIntelligenceLogProto.SettingsIntelligenceEvent event) {
        for (EventLogger logger : mLoggers) {
            logger.log(event);
        }
    }
}
