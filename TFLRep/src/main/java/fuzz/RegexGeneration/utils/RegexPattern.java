package fuzz.RegexGeneration.utils;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegexPattern {
    @Max(value = 5)
    private long alphabetSize;
    private long ssnf;
    private long maxLettersNumber;
}
