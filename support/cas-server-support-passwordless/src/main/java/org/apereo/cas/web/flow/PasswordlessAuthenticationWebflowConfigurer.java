package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.action.EvaluateAction;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link PasswordlessAuthenticationWebflowConfigurer}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class PasswordlessAuthenticationWebflowConfigurer extends AbstractCasWebflowConfigurer {
    /**
     * Transition to obtain username.
     */
    public static final String TRANSITION_ID_PASSWORDLESS_GET_USERID = "passwordlessGetUserId";

    /**
     * passwordless view.
     */
    public static final String STATE_ID_PASSWORDLESS_DISPLAY = "passwordlessDisplayUser";

    private static final String STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION = "acceptPasswordlessAuthentication";
    private static final String STATE_ID_PASSWORDLESS_GET_USERID = "passwordlessGetUserIdView";


    public PasswordlessAuthenticationWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                       final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                       final ApplicationContext applicationContext,
                                                       final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        final Flow flow = getLoginFlow();
        if (flow != null) {
            final ActionState state = getState(flow, CasWebflowConstants.STATE_ID_INIT_LOGIN_FORM, ActionState.class);
            createTransitionForState(state, TRANSITION_ID_PASSWORDLESS_GET_USERID, STATE_ID_PASSWORDLESS_GET_USERID);

            final ViewState viewState = createViewState(flow, STATE_ID_PASSWORDLESS_GET_USERID, "casPasswordlessGetUserIdView");
            createTransitionForState(viewState, CasWebflowConstants.TRANSITION_ID_SUBMIT, STATE_ID_PASSWORDLESS_DISPLAY);

            final ViewState viewStateDisplay = createViewState(flow, STATE_ID_PASSWORDLESS_DISPLAY, "casPasswordlessDisplayView");
            viewStateDisplay.getRenderActionList().add(createEvaluateAction("displayBeforePasswordlessAuthenticationAction"));
            createTransitionForState(viewStateDisplay, CasWebflowConstants.TRANSITION_ID_SUBMIT, STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION);

            final EvaluateAction acceptAction = createEvaluateAction("acceptPasswordlessAuthenticationAction");
            final ActionState acceptState = createActionState(flow, STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION, acceptAction);
            createTransitionForState(acceptState, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, STATE_ID_PASSWORDLESS_DISPLAY);

            final ActionState submission = getState(flow, CasWebflowConstants.STATE_ID_REAL_SUBMIT, ActionState.class);
            final Transition transition = (Transition) submission.getTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS);
            final String targetStateId = transition.getTargetStateId();
            createTransitionForState(acceptState, CasWebflowConstants.TRANSITION_ID_SUCCESS, targetStateId);
            
            registerMultifactorProvidersStateTransitionsIntoWebflow(acceptState);
        }
    }
}
