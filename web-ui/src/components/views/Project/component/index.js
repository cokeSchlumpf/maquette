import './styles.scss'

import _ from 'lodash';
import React from 'react';

import Cards from "../../../elements/Cards";
import ContentContainer from '../../../elements/ContentContainer';
import ContentSection from '../../../elements/ContentSection';
import DatasetCard from "../../../elements/DatasetCard";
import MembersTable from '../../../elements/MembersTable';
import PageBanner from '../../../elements/PageBanner';
import Properties from '../../../elements/Properties';

import {
    Tabs,
    Tab
} from 'carbon-components-react';

export default ({
                    project,
                    projectLoading,
                    projectError,
                    datasets,
                    datasetsLoading,
                    datasetsError,
                    ...props }) => {

    project = project || { name: _.get(props, 'match.params.project') };

    const properties = {
        'Owner': { 'value': _.get(project, 'owner.authorization') },
        'Private': { 'value': (_.get(project, 'private', true) && 'yes') || 'true' },
        'Created': { 'value': _.get(project, 'created') },
        'Created By': { 'value': _.get(project, 'created-by') }
    };

    const activity = {
        'Modified': { 'value': _.get(project, 'modified') },
        'Modified by': { 'value': _.get(project, 'modified-by') }
    };

    return (
        <>
            <PageBanner
                title={ _.get(project, 'name') }
                description={ _.get(project, 'description') }
                centered={ true }
                showDescription={ true }
                tabSpace={ true }
                breadcrumbsItems={
                    [
                        {
                            name: "Browse",
                            to: "/browse"
                        },
                        {
                            name: "Project Details",
                            to: "/projects/" + _.get(project, 'name')
                        }
                    ]
                }
                />

            <ContentContainer>
                <Tabs selected={ 0 }>
                    <Tab label="Assets">
                        <Cards
                            title="Datasets"
                            component={ DatasetCard }
                            cards={ datasets || [] }
                            loading={ datasetsLoading } />

                        <Cards title="Data-Collections" component={ DatasetCard } cards={ [] } />
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

                    <Tab label="Members">
                        <ContentSection title="Members">
                            <MembersTable members={ _.get(project, 'members', []) } />
                        </ContentSection>
                    </Tab>
                </Tabs>
            </ContentContainer>
        </>);
};