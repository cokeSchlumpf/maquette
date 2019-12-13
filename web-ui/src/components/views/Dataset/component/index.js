import './styles.scss'

import _ from 'lodash';
import React from 'react';

import AccessRequestCard from '../../../elements/AccessRequestCard';
import AccessRequestForm from '../../../elements/AccessRequestForm';
import PageBanner from '../../../elements/PageBanner';
import ContentContainer from '../../../elements/ContentContainer';
import ContentSection from '../../../elements/ContentSection';
import Cards from '../../../elements/Cards';
import MembersTable from '../../../elements/MembersTable';
import ProjectCard from '../../../elements/ProjectCard';
import Properties from "../../../elements/Properties";
import DatasetVersionCard from '../../../elements/DatasetVersionCard';

import {
    Accordion,
    AccordionItem,
    Tabs,
    Tab
} from 'carbon-components-react';

const onSubmitAccessRequestDefault = (request) => { console.log(request) };

export default ({ dataset, project, user, versions, onSubmitAccessRequest = onSubmitAccessRequestDefault, ...props }) => {
    const projectName = _.get(dataset, 'project', _.get(props, 'match.params.project', 'no project'));
    const datasetName = _.get(dataset, 'dataset', _.get(props, 'match.params.dataset', 'no dataset'));

    const properties = {
        'Owner': { 'value': _.get(dataset, 'details.owner.authorization', 'n/a') },
        'Private': { 'value': _.get(dataset, 'details.private', 'n/a') },
        'Requires Approval': { 'value': _.get(dataset, 'details.requires-approval') },
        'Data Classification': { 'value': _.get(dataset, 'details.classification', 'n/a') },

        'Created': { 'value': _.get(dataset, 'details.created', 'n/a') },
        'Created By': { 'value': _.get(dataset, 'details.created-by', 'n/a') }
    };

    const activity = {
        'Modified': { 'value': _.get(dataset, 'details.modified', 'n/a') },
        'Modified by': { 'value': _.get(dataset, 'details.modified-by', 'n/a') }
    };

    const onSubmitAccessRequestHandler = (request) => {
        onSubmitAccessRequest(_.assign({}, request, {
            project: projectName,
            dataset: datasetName,
            authorization: 'user',
            to: user.name }))
    };

    const createAccessRequests = (requests) => {
        return (
            <>
                <h3 className="mq--cards-heading">My Access Requests</h3>
                <Accordion>
                    <AccordionItem title="Open (1)" open={ false }>
                        { _.map(requests, r => <AccessRequestCard key={ r.id } data={ r } />) }
                    </AccordionItem>
                    <AccordionItem title="Active (0)">

                    </AccordionItem>
                    <AccordionItem title="Closed (0)" />

                </Accordion>
            </>)
    };

    const createUserAccessRequestTab = () => {
        const accessRequests = _.get(dataset, "access-requests.user-requests", []);

        return (
            <Tab label="Access Requests">
                { _.size(accessRequests) > 0 && createAccessRequests(accessRequests) }

                <h3 className="mq--cards-heading">Create a new Access Request</h3>
                <AccessRequestForm onSubmit={ onSubmitAccessRequestHandler }/>
            </Tab>);
    };

    const createManageAccessRequestsTab = () => (
        <Tab label="Access Requests">
            Manage!
            <AccessRequestForm onSubmit={ onSubmitAccessRequestHandler }/>
        </Tab>);

    const createMembersTab = () => (
        <Tab label="Members">
            <ContentSection title="Members">
                <MembersTable members={ _.get(dataset, 'members', []) } />
            </ContentSection>

            <ContentSection title="Members inherited from project">
                <MembersTable members={ _.get(dataset, 'inherited-members', []) } />
            </ContentSection>
        </Tab>);

    return (
        <>
            <PageBanner
                title={ projectName + "/" + datasetName }
                centered={ true }
                showDescription={ true }
                description={ _.get(dataset, 'details.description') }
                tabSpace={ true }
                breadcrumbsItems={
                    [
                        {
                            name: "Browse",
                            to: "/browse"
                        },
                        {
                            name: "Project Details",
                            to: "/projects/" + projectName
                        },
                        {
                            name: "Dataset Details",
                            to: "/datasets/" + projectName + "/" + datasetName
                        }
                    ]
                }
                />

            <ContentContainer>
                <Tabs selected={ 0 }>
                    <Tab label="Versions">
                        <Cards
                            title="Versions"
                            component={ DatasetVersionCard }
                            cards={ versions || [] }
                            loading={ false } />

                        <Cards title="Data-Collections" component={ ProjectCard } cards={ [] } />
                    </Tab>

                    <Tab label="Details">
                        <ContentSection rows={ true }>
                            <div className="bx--row">
                                <div className="bx--col-md-4">
                                    <ContentSection title="Properties">
                                        <Properties properties={ properties } />
                                    </ContentSection>
                                </div>
                                <div className="bx--col-md-4">
                                    <ContentSection title="Activity">
                                        <Properties properties={ activity } />
                                    </ContentSection>
                                </div>
                            </div>
                        </ContentSection>
                    </Tab>


                    { _.get(dataset, 'can-view-members', false) && createMembersTab() }
                    { _.get(dataset, 'access-requests.can-create-access-request', false) && createUserAccessRequestTab() }
                    { _.get(dataset, 'access-requests.can-manage-access-requests', false) && createManageAccessRequestsTab() }
                </Tabs>
            </ContentContainer>
        </>);
};