package maquette.controller.adapters.cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import lombok.AllArgsConstructor;

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
        } else {
            return String.valueOf(value);
        }
    }

}
