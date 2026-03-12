package de.nikolassv.mdserve;

import com.vladsch.flexmark.util.ast.TextContainer;
import com.vladsch.flexmark.util.sequence.BasedOptionsHolder;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.flexmark.util.sequence.LineInfo;
import com.vladsch.flexmark.util.sequence.builder.ISegmentBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = {
        LineAppendable.Options.class,
        TextContainer.Flags.class,
        ISegmentBuilder.Options.class,
        BasedOptionsHolder.Options.class,
        LineInfo.Flags.class,
})
public class ReflectionConfiguration {
}
