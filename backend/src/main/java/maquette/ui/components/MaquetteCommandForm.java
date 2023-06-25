package maquette.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import maquette.common.Operators;
import maquette.common.commands.Command;
import maquette.common.commands.CommandResult;
import maquette.common.commands.MessageResult;
import maquette.common.functions.Function0;
import maquette.infrastructure.MaquetteSpringCommandRunner;

import java.util.List;

public class MaquetteCommandForm<RESULT extends CommandResult, CMD extends Command<RESULT>> extends VerticalLayout {

    private final BeanValidationBinder<CMD> binder;

    private Function0<CMD> getInitialCommand;

    private final Button save;

    public MaquetteCommandForm(
        BeanValidationBinder<CMD> binder,
        Function0<CMD> getInitialCommand,
        MaquetteSpringCommandRunner commandRunner,
        List<FormLayout> forms) {

        this.binder = binder;
        this.getInitialCommand = getInitialCommand;

        this.setMargin(false);
        this.setPadding(false);

        forms.forEach(this::add);

        save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            var cmd = this.getInitialCommand.get();
            Operators.suppressExceptions(() -> binder.writeBean(cmd));

            try {
                var result = commandRunner.run(cmd);

                if (result instanceof MessageResult<?> msg) {
                    var notification = Notification.show(msg.getMessage());
                    notification.setPosition(Notification.Position.BOTTOM_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }
            } catch (Exception ex) {
                var notification = Notification.show(ex.getMessage());
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        this.add(save);

        binder.addStatusChangeListener(
            status -> save.setEnabled(!status.hasValidationErrors())
        );
    }

    public void setGetInitialCommand(Function0<CMD> getInitialCommand) {
        this.getInitialCommand = getInitialCommand;
        var cmd = this.getInitialCommand.get();

        this.binder.readBean(cmd);
        this.save.setEnabled(true);
    }

}
