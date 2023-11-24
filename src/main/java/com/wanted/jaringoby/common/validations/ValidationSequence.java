package com.wanted.jaringoby.common.validations;

import com.wanted.jaringoby.common.validations.groups.RangeGroup;
import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import com.wanted.jaringoby.common.validations.groups.PatternMatchesGroup;
import jakarta.validation.GroupSequence;

@GroupSequence({MissingValueGroup.class, PatternMatchesGroup.class, RangeGroup.class})
public interface ValidationSequence {

}
