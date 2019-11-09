import './styles.scss'

import _ from 'lodash';
import React from 'react';

import AccessTable from '../../elements/MembersTable';
import PageBanner from '../../elements/PageBanner';
import ContentContainer from '../../elements/ContentContainer';
import ContentSection from '../../elements/ContentSection';
import Cards from "../../elements/Cards";
import ProjectCard from "../../elements/ProjectCard";
import DatasetCard from "../../elements/DatasetCard";

import {
    Checkbox,
    Tabs,
    Tab,
    Select,
    SelectItem,
    TextArea,
    TextInput
} from 'carbon-components-react';

export default (props) => {
    const project = _.get(props, 'match.params.project', 'no project');
    const dataset = _.get(props, 'match.params.dataset', 'no dataset');

    return (
        <>
            <PageBanner
                title={ project + "/" + dataset }
                centered={ true }
                showDescription={ true }
                description="Lorem ipsum dolor"
                tabSpace={ true }
                breadcrumbsItems={
                    [
                        {
                            name: "Browse",
                            to: "/browse"
                        },
                        {
                            name: "Project Details",
                            to: "/projects/" + project
                        },
                        {
                            name: "Dataset Details",
                            to: "/datasets/" + project + "/" + dataset
                        }
                    ]
                }
                />

            <ContentContainer>
                <Tabs selected={ 1 }>
                    <Tab label="Versions">
                        <Cards
                            title="Versions"
                            component={ DatasetCard }
                            cards={ [ "foo" ] }
                            loading={ false } />

                        <Cards title="Data-Collections" component={ ProjectCard } cards={ [] } />
                    </Tab>

                    <Tab label="Properties">
                        <ContentSection title="Settings" rows={ true }>
                            <div className="bx--row">
                                <div className="bx--col-md-4">
                                    <fieldset className="bx--fieldset">
                                        <TextInput
                                            labelText="Project Name"
                                            id="project_name"
                                            disabled={ false }
                                            defaultValue="twitter-analysis" />

                                        <Checkbox
                                            defaultChecked
                                            labelText="Private project"
                                            id="checkbox-label-1"
                                            disabled={ false }
                                            className="mq--project--private-checkbox" />
                                    </fieldset>
                                </div>
                                <div className="bx--col-md-2">
                                    <Select defaultValue="user" labelText="Owner" className="mq--project--owner-type">
                                        <SelectItem value="user" text="User" />
                                        <SelectItem value="role" text="Role" />
                                    </Select>
                                </div>

                                <div className="bx--col-md-2">
                                    <TextInput
                                        labelText="."
                                        id="project_owner"
                                        defaultValue="foo bar" />
                                </div>
                            </div>

                            <div className="bx--row">
                                <div className="bx--col">
                                    <TextArea labelText="Project Description" disabled={ false }>

                                    </TextArea>
                                </div>
                            </div>
                        </ContentSection>

                        <ContentSection title="Members">
                            <AccessTable />
                        </ContentSection>
                    </Tab>

                    <Tab label="AccessRequest">
                    </Tab>
                </Tabs>
            </ContentContainer>
        </>);
};