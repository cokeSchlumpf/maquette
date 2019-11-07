import './styles.scss'

import _ from 'lodash';
import React from 'react';

import AccessTable from '../../../elements/AccessTable';
import PageBanner from '../../../elements/PageBanner';
import ContentContainer from '../../../elements/ContentContainer';
import ContentSection from '../../../elements/ContentSection';
import Cards from "../../../elements/Cards";
import DatasetCard from "../../../elements/DatasetCard";
import Properties from '../../../elements/Properties';

import {
    Checkbox,
    Tabs,
    Tab,
    Select,
    SelectItem,
    TextArea,
    TextInput
} from 'carbon-components-react';

export default ({
                    project,
                    projectLoading,
                    projectError,
                    datasets,
                    datasetsLoading,
                    datasetsError,
                    onInit = () => {},
                    ...props }) => {

    project = project || { name: _.get(props, 'match.params.project') };
    const foo = _.get(project, 'description', 'bla bla');

    console.log(foo);

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
                <Tabs selected={ 1 }>
                    <Tab label="Assets">
                        <Cards
                            title="Datasets"
                            component={ DatasetCard }
                            cards={ datasets || [] }
                            loading={ datasetsLoading } />

                        <Cards title="Data-Collections" component={ DatasetCard } cards={ [] } />
                    </Tab>

                    <Tab label="Properties">
                        <ContentSection rows={ true }>
                            <div className="bx--row">
                                <div className="bx--col-md-4">
                                    <ContentSection title="Properties">
                                        <Properties />
                                    </ContentSection>
                                </div>
                                <div className="bx--col-md-4">
                                    <ContentSection title="Activity">

                                    </ContentSection>
                                </div>
                            </div>


                            <div className="bx--row">
                                <div className="bx--col">

                                </div>
                            </div>
                        </ContentSection>
                    </Tab>

                    <Tab label="Members">
                        <ContentSection title="Members">
                            <AccessTable />
                        </ContentSection>
                    </Tab>
                </Tabs>
            </ContentContainer>
        </>);
};