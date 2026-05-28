package com.dev.quikkkk.core.functional;

import com.dev.quikkkk.modules.nutrition.entity.MacroNutrients;

@FunctionalInterface
public interface MacroExtractor {
    Double get(MacroNutrients macros);
}
