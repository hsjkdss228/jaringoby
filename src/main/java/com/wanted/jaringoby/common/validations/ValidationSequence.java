package com.wanted.jaringoby.common.validations;

import com.wanted.jaringoby.common.validations.groups.ElementCountGroup;
import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import com.wanted.jaringoby.common.validations.groups.PatternMatchesGroup;
import com.wanted.jaringoby.common.validations.groups.RangeGroup;
import jakarta.validation.GroupSequence;

@GroupSequence({
        MissingValueGroup.class,
        PatternMatchesGroup.class,
        RangeGroup.class,
        ElementCountGroup.class
})
public interface ValidationSequence {

}
