import React from 'react';
import './styles.scss';

import Cards from '../../../elements/Cards';
import ContentContainer from '../../../elements/ContentContainer';
import DatasetCard from '../../../elements/DatasetCard';
import PageBanner from '../../../elements/PageBanner';
import ProjectCard from '../../../elements/ProjectCard';
import Search from '../../../elements/Search';

export default ({
                    datasets = [],
                    datasetsLoading = false,
                    projects = [],
                    projectsLoading = false,

                    onClearSearch = () => {},
                    onSearch = () => {}
}) => {

    const onSearchChangeHandler = (value) => {
        if (!value || value.trim().length === 0) {
            onClearSearch();
        } else {
            onSearch(value);
        }
    };

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
                        }
                    ]
                }/>

            <ContentContainer>
                <div className="mq--browse--search">
                    <Search
                        onChange={ onSearchChangeHandler }
                        placeHolderText="Search"
                        name="q" />
                </div>

                <Cards
                   title="Projects"
                   component={ ProjectCard }
                   cards={ projects }
                   loading={ projectsLoading }/>

                <Cards
                    title="Datasets"
                    component={ DatasetCard }
                    cards={ datasets }
                    loading={ datasetsLoading } />

                <Cards title="Data-Collections" component={ ProjectCard } cards={ [] } />
            </ContentContainer>
        </>);
}