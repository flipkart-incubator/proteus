/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.layoutengine.toolbox;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Result
 *
 * @author aditya.sharat
 */
public class Result {

    /**
     * Indicates that a valid {@link JsonElement} was found at the specified data path.
     */
    public static final int RESULT_SUCCESS = 0;

    /**
     * Indicates that the object does not have the specified data path.
     */
    public static final int RESULT_NO_SUCH_DATA_PATH_EXCEPTION = -1;

    /**
     * Indicates that the data path specified is invalid. As an example, looking for a
     * property inside a {@link com.google.gson.JsonPrimitive} or {@link com.google.gson.JsonArray}.
     */
    public static final int RESULT_INVALID_DATA_PATH_EXCEPTION = -2;

    /**
     * Indicates that the data path prematurely led to a {@link com.google.gson.JsonNull}
     */
    public static final int RESULT_JSON_NULL_EXCEPTION = -3;

    /**
     *
     */
    public static final Result NO_SUCH_DATA_PATH_EXCEPTION = new Result(Result.RESULT_NO_SUCH_DATA_PATH_EXCEPTION, null);

    /**
     *
     */
    public static final Result INVALID_DATA_PATH_EXCEPTION = new Result(Result.RESULT_INVALID_DATA_PATH_EXCEPTION, null);

    /**
     *
     */
    public static final Result JSON_NULL_EXCEPTION = new Result(Result.RESULT_JSON_NULL_EXCEPTION, null);

    /**
     * Indicates the return status of the method for a given data path. The return value
     * will be {@code RESULT_SUCCESS} if and only if the data path exists and contains
     * a valid {@link JsonElement}.
     */

    @ResultCode
    public final int RESULT_CODE;
    /**
     * The value at the specified data path.
     * {@code element} will be null if {@code RESULT_CODE} != {@code RESULT_SUCCESS}
     */

    @Nullable
    public final JsonElement element;

    public Result(@ResultCode int RESULT_CODE, @Nullable JsonElement element) {
        this.RESULT_CODE = RESULT_CODE;
        this.element = element;
    }

    /**
     * @param result
     * @return
     */
    public static Result success(JsonElement result) {
        return new Result(RESULT_SUCCESS, result);
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return this.RESULT_CODE == RESULT_SUCCESS;
    }

    @IntDef({RESULT_INVALID_DATA_PATH_EXCEPTION, RESULT_NO_SUCH_DATA_PATH_EXCEPTION, RESULT_SUCCESS, RESULT_JSON_NULL_EXCEPTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResultCode {
    }

}
