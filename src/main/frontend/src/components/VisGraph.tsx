import React, { ReactElement, FC} from "react";
import Graph from "./Graph"
import "../css/vis-network.css"
import { theme } from "../styles";

/**
 * Component that renders an interactive SVG graph
 *  
 * @param graph data 
 * @returns An interactive SVG Graph 
 */
const VisGraph: FC<any> = ({ graph, hierarchical, height }): ReactElement => {
  const options = {
    layout: {
      hierarchical: hierarchical
    },
    edges: {
      color: theme.palette.primary.main,
    },
    height: height,
    nodes: {
      shape: "dot",
      color: theme.palette.primary.main
    },
  };
  
  const events = {
    select: ({ nodes, edges }: any) => {
      console.log("Selected nodes:");
      console.log(nodes);
      console.log("Selected edges:");
      console.log(edges);
    }
  }

  return <Graph
    graph={graph}
    options={options}
    events={events}
    style={{ height: "640px" }}
  />

}

export default VisGraph;