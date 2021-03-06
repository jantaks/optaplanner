/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.config.heuristic.selector.move.generic.chained;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainSwapMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;

@XStreamAlias("subChainSwapMoveSelector")
public class SubChainSwapMoveSelectorConfig extends MoveSelectorConfig {

    private Class<?> entityClass = null;
    @XStreamAlias("subChainSelector")
    private SubChainSelectorConfig subChainSelectorConfig = null;
    @XStreamAlias("secondarySubChainSelector")
    private SubChainSelectorConfig secondarySubChainSelectorConfig = null;

    private Boolean selectReversingMoveToo = null;

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public SubChainSelectorConfig getSubChainSelectorConfig() {
        return subChainSelectorConfig;
    }

    public void setSubChainSelectorConfig(SubChainSelectorConfig subChainSelectorConfig) {
        this.subChainSelectorConfig = subChainSelectorConfig;
    }

    public SubChainSelectorConfig getSecondarySubChainSelectorConfig() {
        return secondarySubChainSelectorConfig;
    }

    public void setSecondarySubChainSelectorConfig(SubChainSelectorConfig secondarySubChainSelectorConfig) {
        this.secondarySubChainSelectorConfig = secondarySubChainSelectorConfig;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntityDescriptor entityDescriptor = deduceEntityDescriptor(
                configPolicy.getSolutionDescriptor(), entityClass);
        SubChainSelectorConfig subChainSelectorConfig_ = subChainSelectorConfig == null ? new SubChainSelectorConfig()
                : subChainSelectorConfig;
        SubChainSelector leftSubChainSelector = subChainSelectorConfig_.buildSubChainSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        SubChainSelectorConfig rightSubChainSelectorConfig = secondarySubChainSelectorConfig == null
                ? subChainSelectorConfig_ : secondarySubChainSelectorConfig;
        SubChainSelector rightSubChainSelector = rightSubChainSelectorConfig.buildSubChainSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new SubChainSwapMoveSelector(leftSubChainSelector, rightSubChainSelector, randomSelection,
                selectReversingMoveToo == null ? true : selectReversingMoveToo);
    }

    public void inherit(SubChainSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass,
                inheritedConfig.getEntityClass());
        if (subChainSelectorConfig == null) {
            subChainSelectorConfig = inheritedConfig.getSubChainSelectorConfig();
        } else if (inheritedConfig.getSubChainSelectorConfig() != null) {
            subChainSelectorConfig.inherit(inheritedConfig.getSubChainSelectorConfig());
        }
        if (secondarySubChainSelectorConfig == null) {
            secondarySubChainSelectorConfig = inheritedConfig.getSecondarySubChainSelectorConfig();
        } else if (inheritedConfig.getSecondarySubChainSelectorConfig() != null) {
            secondarySubChainSelectorConfig.inherit(inheritedConfig.getSecondarySubChainSelectorConfig());
        }
        selectReversingMoveToo = ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo,
                inheritedConfig.getSelectReversingMoveToo());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelectorConfig
                + (secondarySubChainSelectorConfig == null ? "" : ", " + secondarySubChainSelectorConfig) + ")";
    }

}
