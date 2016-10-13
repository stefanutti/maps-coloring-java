while is_the_end_of_the_reduction_process is False:
    f1 = g_faces[0]
    while is_the_edge_to_remove_found is False and i_edge < len_of_the_face_to_reduce:
        edge_to_remove = f1[i_edge]
        f1_plus_f2_temp = join_faces(f1, f2, edge_to_remove)

        if is_the_graph_one_edge_connected(f1_plus_f2_temp) is True:
            i_edge += 1
        else:
            is_the_edge_to_remove_found = True

    if len_of_the_face_to_reduce == 2:
        g_faces.remove(f1)
        g_faces.remove(f2)
        g_faces.append(f1_plus_f2_temp)

        third_face_to_update = next(face for face in g_faces if check_if_vertex_is_in_face(face, v1))
        remove_vertex_from_face(third_face_to_update, v1)
        remove_vertex_from_face(third_face_to_update, v2)  # For this F2 case, the two vertices belong to only a third face
    else:
        g_faces.remove(f1)
        g_faces.remove(f2)
        g_faces.append(f1_plus_f2_temp)

        third_face_to_update = next(face for face in g_faces if check_if_vertex_is_in_face(face, v1))
        fourth_face_to_update = next(face for face in g_faces if check_if_vertex_is_in_face(face, v2))

    f_temp = next((f for f in g_faces if len(f) == 2), next((f for f in g_faces if len(f) == 3), next((f for f in g_faces if len(f) == 4), next((f for f in g_faces if len(f) == 5), g_faces[0]))))

while is_the_end_of_the_rebuild_process is False:

    # Get the string to walk back home
    #
    ariadne_step = ariadne_string.pop()
    if logger.isEnabledFor(logging.INFO): logger.info("ariadne_step: %s", ariadne_step)

    # F2 = [2, v1, v2, vertex_to_join_near_v1, vertex_to_join_near_v2]
    #
    if ariadne_step[0] == 2:

        # CASE: F2
        # Update stats
        #
        stats['CASE-F2-01'] += 1

        if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F2 (multiple edge)")
        if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))

        v1 = ariadne_step[1]
        v2 = ariadne_step[2]
        vertex_to_join_near_v1 = ariadne_step[3]
        vertex_to_join_near_v2 = ariadne_step[4]

        # For F2 to compute the new colors is easy
        #
        previous_edge_color = get_edge_color(the_colored_graph, (vertex_to_join_near_v1, vertex_to_join_near_v2))

        # Choose available colors
        #
        new_multiedge_color_one = get_the_other_colors([previous_edge_color])[0]
        new_multiedge_color_two = get_the_other_colors([previous_edge_color])[1]
        if logger.isEnabledFor(logging.DEBUG): logger.debug("new_multiedge_color_one: %s, new_multiedge_color_two: %s", new_multiedge_color_one, new_multiedge_color_two)

        # Delete the edge
        #
        the_colored_graph.delete_edge((vertex_to_join_near_v1, vertex_to_join_near_v2, previous_edge_color))

        # Restore the previous edge
        #
        the_colored_graph.add_edge(v1, vertex_to_join_near_v1, previous_edge_color)
        the_colored_graph.add_edge(v2, vertex_to_join_near_v2, previous_edge_color)
        the_colored_graph.add_edge(v1, v2, new_multiedge_color_one)
        the_colored_graph.add_edge(v2, v1, new_multiedge_color_two)

        if logger.isEnabledFor(logging.INFO): logger.info("previous_edge_color: %s, new_multiedge_color_one: %s, new_multiedge_color_two: %s", previous_edge_color, new_multiedge_color_one, new_multiedge_color_two)

        # if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))
        # if is_well_colored(the_colored_graph) is False:
        #     logger.error("Unexpected condition (Not well colored). Mario you'd better go back to paper")
        #     exit(-1)

        if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F2 (multiple edge)")

    elif ariadne_step[0] == 3:

        # CASE: F3
        # [x, v1, v2, vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v2_not_on_the_face]
        # Update stats
        #
        stats['CASE-F3-01'] += 1
        if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F3")
        if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))

        v1 = ariadne_step[1]
        v2 = ariadne_step[2]
        vertex_to_join_near_v1_on_the_face = ariadne_step[3]
        vertex_to_join_near_v2_on_the_face = ariadne_step[4]
        vertex_to_join_near_v1_not_on_the_face = ariadne_step[5]
        vertex_to_join_near_v2_not_on_the_face = ariadne_step[6]

        # For F3 to compute the new colors is easy (check also if it is a multiple edges)
        #
        if logger.isEnabledFor(logging.DEBUG): logger.debug("vertex_to_join_near_v1_on_the_face: %s, vertex_to_join_near_v2_on_the_face: %s, vertex_to_join_near_v1_not_on_the_face: %s, vertex_to_join_near_v2_not_on_the_face: %s", vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v2_not_on_the_face)

        # If e1 and e2 have the same vertices, they are the same multiedge
        #
        if (vertex_to_join_near_v1_on_the_face == vertex_to_join_near_v2_on_the_face) and (vertex_to_join_near_v1_not_on_the_face == vertex_to_join_near_v2_not_on_the_face):

            # Get the colors of the two edges (multiedge). Select only the two multiedges (e1, e2 with same vertices)
            #
            tmp_multiple_edges_to_check = the_colored_graph.edges_incident(vertex_to_join_near_v1_on_the_face)  # Three edges will be returned
            multiple_edges_to_check = [(va, vb, l) for (va, vb, l) in tmp_multiple_edges_to_check if (vertex_to_join_near_v1_not_on_the_face == va) or (vertex_to_join_near_v1_not_on_the_face == vb)]
            previous_edge_color_at_v1 = multiple_edges_to_check[0][2]
            previous_edge_color_at_v2 = multiple_edges_to_check[1][2]
        else:
            previous_edge_color_at_v1 = get_edge_color(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face))
            previous_edge_color_at_v2 = get_edge_color(the_colored_graph, (vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face))

        if logger.isEnabledFor(logging.DEBUG): logger.debug("previous_edge_color_at_v1: %s, previous_edge_color_at_v2: %s", previous_edge_color_at_v1, previous_edge_color_at_v2)

        # Checkpoint
        #
        if previous_edge_color_at_v1 == previous_edge_color_at_v2:
            logger.error("Unexpected condition (for F3 faces two edges have a vertex in common, and so colors MUST be different at this point). Mario you'd better go back to paper")
            exit(-1)

        # Choose a different color
        #
        new_edge_color = get_the_other_colors([previous_edge_color_at_v1, previous_edge_color_at_v2])[0]

        # Delete the edges
        # Since e1 and e2 may be the same multiedge or maybe separately on different multiedge, I remove them using also the "label" parameter
        #
        the_colored_graph.delete_edge((vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1))
        the_colored_graph.delete_edge((vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2))

        # Restore the previous edge
        #
        the_colored_graph.add_edge(v1, vertex_to_join_near_v1_on_the_face, previous_edge_color_at_v2)
        the_colored_graph.add_edge(v1, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1)
        the_colored_graph.add_edge(v2, vertex_to_join_near_v2_on_the_face, previous_edge_color_at_v1)
        the_colored_graph.add_edge(v2, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2)
        the_colored_graph.add_edge(v1, v2, new_edge_color)

        if logger.isEnabledFor(logging.DEBUG): logger.debug("previous_edge_color_at_v1: %s, previous_edge_color_at_v2: %s, new_edge_color: %s", previous_edge_color_at_v1, previous_edge_color_at_v2, new_edge_color)

        # if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))
        # if is_well_colored(the_colored_graph) is False:
        #     logger.error("Unexpected condition (Not well colored). Mario you'd better go back to paper")
        #     exit(-1)

        if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F3")

    elif ariadne_step[0] == 4:

        # CASE: F4
        # [x, v1, v2, vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v2_not_on_the_face]
        #
        if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F4")
        if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))

        v1 = ariadne_step[1]
        v2 = ariadne_step[2]
        vertex_to_join_near_v1_on_the_face = ariadne_step[3]
        vertex_to_join_near_v2_on_the_face = ariadne_step[4]
        vertex_to_join_near_v1_not_on_the_face = ariadne_step[5]
        vertex_to_join_near_v2_not_on_the_face = ariadne_step[6]

        # For F4 to compute the new colors is not so easy
        #
        previous_edge_color_at_v1 = get_edge_color(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face))
        previous_edge_color_at_v2 = get_edge_color(the_colored_graph, (vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face))
        if logger.isEnabledFor(logging.DEBUG): logger.debug("previous_edge_color_at_v1: %s, previous_edge_color_at_v2: %s", previous_edge_color_at_v1, previous_edge_color_at_v2)

        # For an F4, the top edge is the edge not adjacent to the edge to restore (as in a rectangular area)
        #
        edge_color_of_top_edge = get_edge_color(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face))
        if logger.isEnabledFor(logging.DEBUG): logger.debug("edge_color_of_top_edge: %s", edge_color_of_top_edge)

        # Handle the different cases
        #
        if previous_edge_color_at_v1 == previous_edge_color_at_v2:

            # Update stats
            #
            stats['CASE-F4-01'] += 1
            if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F4 - Same color at v1 and v2")
            if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))

            # CASE: F4 SUBCASE: Same color at v1 and v2
            # Since edges at v1 and v2 are on the same Kempe cycle (with the top edge), I can also avoid the kempe chain color switching, since in this case the chain is made of three edges
            #
            the_colored_graph.delete_edge((vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1))
            the_colored_graph.delete_edge((vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2))

            # Kempe chain color swap is done manually since the chain is only three edges long
            #
            the_colored_graph.add_edge(v1, vertex_to_join_near_v1_on_the_face, edge_color_of_top_edge)
            the_colored_graph.add_edge(v2, vertex_to_join_near_v2_on_the_face, edge_color_of_top_edge)

            # Just for sure. Is the top edge a multiedge? I need to verify it. It should't be
            #
            if is_multiedge(the_colored_graph, vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face):
                the_colored_graph.delete_edge(vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, edge_color_of_top_edge)
                the_colored_graph.add_edge(vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, previous_edge_color_at_v1)
                logger.error("HERE?")   # This is only to verify if this condition is real
                exit(-1)
            else:
                the_colored_graph.set_edge_label(vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, previous_edge_color_at_v1)

            # Restore the other edges
            #
            the_colored_graph.add_edge(v1, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1)
            the_colored_graph.add_edge(v2, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2)
            the_colored_graph.add_edge(v1, v2, get_the_other_colors([previous_edge_color_at_v1, edge_color_of_top_edge])[0])

            # if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))
            # if is_well_colored(the_colored_graph) is False:
            #     logger.error("Unexpected condition (Not well colored). Mario you'd better go back to paper")
            #     exit(-1)

            if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F4 - Same color at v1 and v2")
        else:

            # In this case I have to check if the edges at v1 and v2 are on the same Kempe cycle
            #
            if are_edges_on_the_same_kempe_cycle(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face), (vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face), previous_edge_color_at_v1, previous_edge_color_at_v2) is True:

                # Update stats
                #
                stats['CASE-F4-02'] += 1

                if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F4 - The two edges are on the same Kempe cycle")
                if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))

                # CASE: F4, SUBCASE: The two edges are on the same Kempe cycle
                # Since edges at v1 and v2 are on the same Kempe cycle, apply half Kempe cycle color swapping
                #
                # I broke the cycle to apply the half Kempe chain color swapping
                #
                the_colored_graph.delete_edge((vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1))
                the_colored_graph.delete_edge((vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2))
                the_colored_graph.add_edge(v1, vertex_to_join_near_v1_on_the_face, previous_edge_color_at_v1)
                the_colored_graph.add_edge(v2, vertex_to_join_near_v2_on_the_face, previous_edge_color_at_v2)

                # Half Kempe chain color swapping
                #
                kempe_chain_color_swap(the_colored_graph, (v1, vertex_to_join_near_v1_on_the_face), previous_edge_color_at_v1, previous_edge_color_at_v2)

                # Restore the other edges
                #
                the_colored_graph.add_edge(v1, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1)
                the_colored_graph.add_edge(v2, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2)
                the_colored_graph.add_edge(v1, v2, get_the_other_colors([previous_edge_color_at_v1, previous_edge_color_at_v2])[0])

                # if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))
                # if is_well_colored(the_colored_graph) is False:
                #     logger.error("Unexpected condition (Not well colored). Mario you'd better go back to paper")
                #     exit(-1)

                if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F4 - The two edges are on the same Kempe cycle")

            else:

                # Update stats
                #
                stats['CASE-F4-03'] += 1

                if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F4 - The two edges are NOT on the same Kempe cycle")
                if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))

                # CASE: F4 SUBCASE: Worst case: The two edges are NOT on the same Kempe cycle
                # I'll rotate the colors of the cycle for the edge at v1, and then, since edge_color_at_v1 will be == edge_color_at_v2, apply CASE-001
                #
                kempe_chain_color_swap(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face), previous_edge_color_at_v1, get_the_other_colors([previous_edge_color_at_v1, edge_color_of_top_edge])[0])
                previous_edge_color_at_v1 = previous_edge_color_at_v2

                # CASE: F4, SUBCASE: The two edges are now on the same Kempe cycle
                #
                the_colored_graph.delete_edge((vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1))
                the_colored_graph.delete_edge((vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2))

                # Kempe chain color swap is done manually since the chain is only three edges long
                #
                the_colored_graph.add_edge(v1, vertex_to_join_near_v1_on_the_face, edge_color_of_top_edge)
                the_colored_graph.add_edge(v2, vertex_to_join_near_v2_on_the_face, edge_color_of_top_edge)

                # Just to be sure. Is the top edge a multiedge? I need to verify it. It should't be
                #
                if is_multiedge(the_colored_graph, vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face):
                    the_colored_graph.delete_edge(vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, edge_color_of_top_edge)
                    the_colored_graph.add_edge(vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, previous_edge_color_at_v1)
                    logger.error("HERE?")   # This is only to verify if this condition is real
                    exit(-1)
                else:
                    the_colored_graph.set_edge_label(vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, previous_edge_color_at_v1)

                # Restore the other edges
                #
                the_colored_graph.add_edge(v1, vertex_to_join_near_v1_not_on_the_face, previous_edge_color_at_v1)
                the_colored_graph.add_edge(v2, vertex_to_join_near_v2_not_on_the_face, previous_edge_color_at_v2)
                the_colored_graph.add_edge(v1, v2, get_the_other_colors([previous_edge_color_at_v1, edge_color_of_top_edge])[0])

                # if logger.isEnabledFor(logging.DEBUG): logger.debug("Edges: %s, is_regular: %s", list(the_colored_graph.edge_iterator(labels = True)), the_colored_graph.is_regular(3))
                # if is_well_colored(the_colored_graph) is False:
                #     logger.error("Unexpected condition (Not well colored). Mario you'd better go back to paper")
                #     exit(-1)

                if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F4 - The two edges are NOT on the same Kempe cycle")

        if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F4")

    elif ariadne_step[0] == 5:

        # CASE: F5
        # [x, v1, v2, vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v2_not_on_the_face]
        #
        if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: restore an F5")

        v1 = ariadne_step[1]
        v2 = ariadne_step[2]
        vertex_to_join_near_v1_on_the_face = ariadne_step[3]
        vertex_to_join_near_v2_on_the_face = ariadne_step[4]
        vertex_to_join_near_v1_not_on_the_face = ariadne_step[5]
        vertex_to_join_near_v2_not_on_the_face = ariadne_step[6]

        # I have to get the two edges that are on top
        # These are two edges that have near_v1_on_the_face and near_v2_on_the_face and a shared vertex
        # First thing: I need to get the vertex_in_the_top_middle
        #
        edges_at_vertices_near_v1_on_the_face = the_colored_graph.edges_incident([vertex_to_join_near_v1_on_the_face], labels = False)
        edges_at_vertices_near_v2_on_the_face = the_colored_graph.edges_incident([vertex_to_join_near_v2_on_the_face], labels = False)
        tmp_v1 = [item for sublist in edges_at_vertices_near_v1_on_the_face for item in sublist]
        tmp_v1.remove(vertex_to_join_near_v1_not_on_the_face)
        tmp_v2 = [item for sublist in edges_at_vertices_near_v2_on_the_face for item in sublist]
        tmp_v2.remove(vertex_to_join_near_v2_not_on_the_face)
        vertex_in_the_top_middle = list(set.intersection(set(tmp_v1), set(tmp_v2)))[0]

        # The algorithm:
        #
        # - Check if c1 and c2 are on the same Kempe chain
        # - If not, try a random swap
        #   - First try a swap starting from an edge on the face
        #   - Then try a swap starting from a random edge of the kempe loop on v1
        #
        end_of_f5_restore = False
        i_attempt = 0
        while end_of_f5_restore is False:

            # For F5 to compute the new colors is difficult (and needs to be proved if always works in all cases)
            # I need to handle the different cases
            #
            c1 = get_edge_color(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_to_join_near_v1_not_on_the_face))
            c3 = get_edge_color(the_colored_graph, (vertex_to_join_near_v1_on_the_face, vertex_in_the_top_middle))
            c4 = get_edge_color(the_colored_graph, (vertex_in_the_top_middle, vertex_to_join_near_v2_on_the_face))
            c2 = get_edge_color(the_colored_graph, (vertex_to_join_near_v2_on_the_face, vertex_to_join_near_v2_not_on_the_face))

            # F5-C1
            #
            if c1 == c2:

                # The four edges are: c1, c3, c4, c2==c1
                # In case e1 and e2 are not on the same Kempe loop (c1, c3) or (c2, c4), the switch of the top colors (c3, c4) solves (I hope) the situation
                #
                if are_edges_on_the_same_kempe_cycle(the_colored_graph, (vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v1_on_the_face), (vertex_to_join_near_v2_not_on_the_face, vertex_to_join_near_v2_on_the_face), c1, c3):

                    if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: CASE-F5-C1==C2-SameKempeLoop-C1-C3")

                    # Apply half Kempe loop color switching (c1, c3)
                    #
                    apply_half_kempe_loop_color_switching(the_colored_graph, ariadne_step, c1, c1, c1, c3)
                    end_of_f5_restore = True

                    # Update stats
                    #
                    stats['CASE-F5-C1==C2-SameKempeLoop-C1-C3'] += 1

                    if logger.isEnabledFor(logging.INFO): logger.info("END: CASE-F5-C1==C2-SameKempeLoop-C1-C3")

                elif are_edges_on_the_same_kempe_cycle(the_colored_graph, (vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v1_on_the_face), (vertex_to_join_near_v2_not_on_the_face, vertex_to_join_near_v2_on_the_face), c1, c4):

                    if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: CASE-F5-C1==C2-SameKempeLoop-C1-C4")

                    # Apply half Kempe loop color switching (c2==c1, c4)
                    #
                    apply_half_kempe_loop_color_switching(the_colored_graph, ariadne_step, c1, c1, c1, c4)
                    end_of_f5_restore = True

                    # Update stats
                    #
                    stats['CASE-F5-C1==C2-SameKempeLoop-C1-C4'] += 1

                    if logger.isEnabledFor(logging.INFO): logger.info("END: CASE-F5-C1==C2-SameKempeLoop-C1-C4")
            else:

                # In case e1 and e2 are not on the same Kempe loop (c1, c2), the swap of c2, c1 at e2 will give the the first case
                #
                if are_edges_on_the_same_kempe_cycle(the_colored_graph, (vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v1_on_the_face), (vertex_to_join_near_v2_not_on_the_face, vertex_to_join_near_v2_on_the_face), c1, c2):

                    if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: CASE-F5-C1!=C2-SameKempeLoop-C1-C4")

                    # Apply half Kempe loop color switching (c1, c2)
                    #
                    apply_half_kempe_loop_color_switching(the_colored_graph, ariadne_step, c1, c2, c1, c2)
                    end_of_f5_restore = True

                    # Update stats
                    #
                    stats['CASE-F5-C1!=C2-SameKempeLoop-C1-C2'] += 1

                    if logger.isEnabledFor(logging.INFO): logger.info("END: CASE-F5-C1!=C2-SameKempeLoop-C1-C2")
                else:
                    xxx = 0
                    # get_random_edge_of_a_kempe_loop(the_colored_graph, (vertex_to_join_near_v1_not_on_the_face, vertex_to_join_near_v1_on_the_face), c1, c2)

            # Try random switches around the graph for a random few times
            #
            if end_of_f5_restore is False:

                # Attempts to change (swap) something in the graph
                # TODO: use an edge along one of the disjoined Kempe loops at v1 and v2
                #
                stats['RANDOM_KEMPE_SWITCHES'] += 1

                random_color_number = randint(0, 1)
                edge_color_of_random_edge = the_colored_graph.random_edge(labels = False)
                random_color = get_edge_color(the_colored_graph, edge_color_of_random_edge)
                kempe_chain_color_swap(the_colored_graph, edge_color_of_random_edge, random_color, get_the_other_colors([random_color])[random_color_number])
                if logger.isEnabledFor(logging.INFO): logger.info("Random: %s, random_edge: %s, random_color: %s", stats['RANDOM_KEMPE_SWITCHES'], edge_color_of_random_edge, random_color)

                # Only for debug: which map is causing this impasse?
                #
                if i_attempt == 1000:
                    the_colored_graph.allow_multiple_edges(False)  # At this point there are no multiple edge
                    the_colored_graph.export_to_file("debug_really_bad_case.edgelist", format = "edgelist")

        # END F5 has been restored
        #
        if logger.isEnabledFor(logging.INFO): logger.info("END: restore an F5: %s", stats['RANDOM_KEMPE_SWITCHES'])

    # Separator
    #
    if logger.isEnabledFor(logging.INFO): logger.info("")

    # After all cases
    #
    if not is_well_colored(the_colored_graph):
        logger.error("Unexpected condition (coloring is not valid). Mario you'd better go back to paper or learn to code")
        exit(-1)

    # If no other edges are to be restored, then I've done
    #
    if len(ariadne_string) == 0:
        is_the_end_of_the_rebuild_process = True

stats['time_ELABORATION_END'] = time.ctime()
if logger.isEnabledFor(logging.INFO): logger.info("-------------------------")
if logger.isEnabledFor(logging.INFO): logger.info("END: Reconstruction phase")
if logger.isEnabledFor(logging.INFO): logger.info("-------------------------")
if logger.isEnabledFor(logging.INFO): logger.info("")

#######
#######
# 4CT : Show the restored and 4 colored map
#######
#######

if logger.isEnabledFor(logging.INFO): logger.info("------------------------------------------")
if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: Show the restored and 4 colored map")
if logger.isEnabledFor(logging.INFO): logger.info("------------------------------------------")

# Now I can restore the multiedge flag
#
if the_colored_graph.has_multiple_edges():
    logger.error("Unexpected condition (recreated graph has multiple edges at the end). Mario you'd better go back to paper")
    exit(-1)

the_colored_graph.allow_multiple_edges(False)  # At this point there are no multiple edge

# Check if the recreated graph is isomorphic to the original
#
if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: Check if isomorphic")
is_isomorphic = the_graph.is_isomorphic(the_colored_graph)
if logger.isEnabledFor(logging.INFO): logger.info("END: Check if isomorphic")

if is_isomorphic is False:
    logger.error("Unexpected condition (recreated graph is different from the original). Mario you'd better go back to paper")

if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: print_graph (Original)")
print_graph(the_graph)
if logger.isEnabledFor(logging.INFO): logger.info("END: print_graph (Original)")
if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: print_graph (Colored)")
print_graph(the_colored_graph)
if logger.isEnabledFor(logging.INFO): logger.info("END: print_graph (Colored)")

if logger.isEnabledFor(logging.INFO): logger.info("----------------------------------------")
if logger.isEnabledFor(logging.INFO): logger.info("END: Show the restored and 4 colored map")
if logger.isEnabledFor(logging.INFO): logger.info("----------------------------------------")

# Save the output graph
#
if args.output is not None:

    # Possibilities: adjlist, dot, edgelist, gexf, gml, graphml, multiline_adjlist, pajek, yaml
    # Format chosen: edgelist
    #
    if logger.isEnabledFor(logging.INFO): logger.info("------------------------------------------------")
    if logger.isEnabledFor(logging.INFO): logger.info("BEGIN: Save the 4 colored map in edgelist format")
    if logger.isEnabledFor(logging.INFO): logger.info("------------------------------------------------")

    the_colored_graph.export_to_file(args.output, format = "edgelist")
    if logger.isEnabledFor(logging.INFO): logger.info("File saved: %s", args.output)

    if logger.isEnabledFor(logging.INFO): logger.info("----------------------------------------------")
    if logger.isEnabledFor(logging.INFO): logger.info("END: Save the 4 colored map in edgelist format")
    if logger.isEnabledFor(logging.INFO): logger.info("----------------------------------------------")

# Print statistics
#
print_stats()
