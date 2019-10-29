package maquette.controller.domain.api.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.governance.DataClassification;

@Value
@AllArgsConstructor(staticName = "apply")
public final class OutputFormat {

    private final DateFormat dateFormat;

    public static OutputFormat apply() {
        return apply(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"));
    }

    public String format(Object value) {
        if (value instanceof Optional) {
            Optional<?> opt = (Optional<?>) value;

            if (opt.isPresent()) {
                return format(opt.get());
            } else {
                return "-";
            }
        } else if (value instanceof Date) {
            return dateFormat.format((Date) value);
        } else if (value instanceof Instant) {
            return dateFormat.format(Date.from((Instant) value));
        } else if (value instanceof Boolean) {
            boolean b = (Boolean) value;
            return b ? "yes" : "no";
        } else if (value instanceof DataClassification) {
            return ((DataClassification) value).name();
        } else {
            return String.valueOf(value);
        }
    }

}
