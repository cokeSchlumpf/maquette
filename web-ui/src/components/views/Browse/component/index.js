import React from 'react';
import './styles.scss';

import Cards from '../../../elements/Cards';
import ContentContainer from '../../../elements/ContentContainer';
import DatasetCard from '../../../elements/DatasetCard';
import PageBanner from '../../../elements/PageBanner';
import ProjectCard from '../../../elements/ProjectCard';
import Search from '../../../elements/Search';

import {
    DataTable,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableHeader,
    TableRow,
} from 'carbon-components-react';

export const initialRows = [
    {
        id: 'a',
        name: 'Load Balancer 3',
        protocol: 'HTTP',
        port: 3000,
        rule: 'Round robin',
        attached_groups: 'Kevins VM Groups',
        status: 'Disabled',
    },
    {
        id: 'b',
        name: 'Load Balancer 1',
        protocol: 'HTTP',
        port: 443,
        rule: 'Round robin',
        attached_groups: 'Maureens VM Groups',
        status: 'Starting',
    },
    {
        id: 'c',
        name: 'Load Balancer 2',
        protocol: 'HTTP',
        port: 80,
        rule: 'DNS delegation',
        attached_groups: 'Andrews VM Groups',
        status: 'Active',
    },
];

export const headers = [
    {
        key: 'name',
        header: 'Name',
    },
    {
        key: 'protocol',
        header: 'Protocol',
    },
    {
        key: 'port',
        header: 'Port',
    },
    {
        key: 'rule',
        header: 'Rule',
    },
    {
        key: 'attached_groups',
        header: 'Attached Groups',
    },
    {
        key: 'status',
        header: 'Status',
    },
];

export default (props) => {
    return (
        <>
            <PageBanner
                title="Browse Assets"
                centered={ true }
                showDescription={ false }
                breadcrumbsItems={
                    [
                        {
                            name: "Browse",
                            to: "/browse"
                        },
                        {
                            name: "Search Results",
                            to: "/browse?q=foo"
                        }
                    ]
                }/>

            <ContentContainer>
                <div className="mq--browse--search">
                    <Search
                        onChange={ console.log }
                        placeHolderText="Search"
                        name="q" />
                </div>

                <Cards title="Projects" component={ ProjectCard } cards={ [{}, {}, {}, {}, {}, {}, {}] } />

                <Cards title="Datasets" component={ DatasetCard } cards={ [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}] } />

                <Cards title="Data-Collections" component={ ProjectCard } cards={ [] } />
            </ContentContainer>
        </>);
}