/**
 * Copyright 2014 Daum Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.auth.common;

/**
 * @author leoshin on 15. 9. 4.
 */
public class PageableContext {
    private String beforeUrl;
    private String afterUrl;

    public synchronized String getBeforeUrl() {
        return beforeUrl;
    }

    public synchronized String getAfterUrl() {
        return afterUrl;
    }

    public synchronized void setBeforeUrl(String beforeUrl) {
        this.beforeUrl = beforeUrl;
    }

    public synchronized void setAfterUrl(String afterUrl) {
        this.afterUrl = afterUrl;
    }

    /**
     * 다음 요청이 가능한지 여부를 알려준다.
     * @return 다음요청을 할 수 있는지에대한 여부.
     */
    public synchronized boolean hasNext() {
        if (beforeUrl != null && afterUrl != null) {
            return true;
        }

        if (beforeUrl == null && afterUrl != null) {
            return true;
        }

        return false;
    }
}
