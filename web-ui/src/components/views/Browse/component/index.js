import React from 'react';
import './styles.scss';

import Cards from '../../../elements/Cards';
import ContentContainer from '../../../elements/ContentContainer';
import DatasetCard from '../../../elements/DatasetCard';
import PageBanner from '../../../elements/PageBanner';
import ProjectCard from '../../../elements/ProjectCard';
import Search from '../../../elements/Search';

export default ({ projects }) => {

    console.log(projects);

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
                        onChange={ console.log }
                        placeHolderText="Search"
                        name="q" />
                </div>

                <Cards title="Projects" component={ ProjectCard } cards={ projects } />

                <Cards title="Datasets" component={ DatasetCard } cards={ [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}] } />

                <Cards title="Data-Collections" component={ ProjectCard } cards={ [] } />
            </ContentContainer>
        </>);
}