/*
 * Copyright 2020 WaterdogTEAM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package pe.waterdog.network.rewrite.types;

public class ItemEntry {

    private final String identifier;
    private final int runtimeId;
    private final int hash;

    public ItemEntry(String identifier, int runtimeId) {
        this.identifier = identifier;
        this.runtimeId = runtimeId;
        this.hash = super.hashCode();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }
}