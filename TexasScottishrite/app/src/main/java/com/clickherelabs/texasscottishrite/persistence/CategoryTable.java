/*
 * Copyright 2015 Google Inc.
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

package com.clickherelabs.texasscottishrite.persistence;
import android.provider.BaseColumns;

/**
 * Structure of the category table.
 */
public interface CategoryTable {

    String NAME = "category";

    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_NAME = "name";
    String COLUMN_THEME = "theme";
    String COLUMN_SCORES = "scores";
    String COLUMN_SOLVED = "solved";
    String COLUMN_ACTION = "action";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME,
            COLUMN_THEME, COLUMN_SOLVED, COLUMN_SCORES, COLUMN_ACTION};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_THEME + " TEXT NOT NULL, "
            + COLUMN_SOLVED + " TEXT NOT NULL, "
            + COLUMN_SCORES + " TEXT, "
            + COLUMN_ACTION + " TEXT);";
}
